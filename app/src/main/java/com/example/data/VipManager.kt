package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

class VipManager(private val appContext: Context) {

    private val prefs: SharedPreferences =
        appContext.getSharedPreferences("vip_secure_v2_prefs", Context.MODE_PRIVATE)

    // Google Login State (Mandatory before using app)
    private val _signedInEmail = MutableStateFlow(prefs.getString(KEY_GOOGLE_EMAIL, null))
    val signedInEmail: StateFlow<String?> = _signedInEmail.asStateFlow()

    private val _signedInName = MutableStateFlow(prefs.getString(KEY_GOOGLE_NAME, null))
    val signedInName: StateFlow<String?> = _signedInName.asStateFlow()

    // Strict Owner Check: ONLY shahistatabassum9@gmail.com can modify QR Code or UPI/UID
    private val _isOwnerAdmin = MutableStateFlow(checkIsOwner(prefs.getString(KEY_GOOGLE_EMAIL, null)))
    val isOwnerAdmin: StateFlow<Boolean> = _isOwnerAdmin.asStateFlow()

    // Strict VIP Verification: Wipes any old glitch unlock that lacks a cryptographic verification token
    private val _isVipUnlocked = MutableStateFlow(verifyStoredVipState())
    val isVipUnlocked: StateFlow<Boolean> = _isVipUnlocked.asStateFlow()

    private val _activePlan = MutableStateFlow(
        if (_isVipUnlocked.value) (prefs.getString(KEY_ACTIVE_PLAN, PLAN_MONTHLY) ?: PLAN_MONTHLY) else "NONE"
    )
    val activePlan: StateFlow<String> = _activePlan.asStateFlow()

    private val _paymentQrUri = MutableStateFlow<String?>(prefs.getString(KEY_PAYMENT_QR_URI, null))
    val paymentQrUri: StateFlow<String?> = _paymentQrUri.asStateFlow()

    private val _creatorUpi = MutableStateFlow(
        prefs.getString(KEY_CREATOR_UPI, DEFAULT_OWNER_UPI) ?: DEFAULT_OWNER_UPI
    )
    val creatorUpi: StateFlow<String> = _creatorUpi.asStateFlow()

    private val _creatorUid = MutableStateFlow(
        prefs.getString(KEY_CREATOR_UID, DEFAULT_OWNER_UID) ?: DEFAULT_OWNER_UID
    )
    val creatorUid: StateFlow<String> = _creatorUid.asStateFlow()

    private fun checkIsOwner(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        return email.trim().equals(OWNER_ADMIN_EMAIL, ignoreCase = true)
    }

    private fun verifyStoredVipState(): Boolean {
        val unlocked = prefs.getBoolean(KEY_VIP_UNLOCKED, false)
        val storedUtr = prefs.getString(KEY_VERIFIED_UTR, null)
        val storedSig = prefs.getString(KEY_VERIFIED_SIG, null)
        val email = prefs.getString(KEY_GOOGLE_EMAIL, null) ?: ""
        if (!unlocked || storedUtr.isNullOrBlank() || storedSig.isNullOrBlank()) {
            return false
        }
        val expectedSig = computeSignature(email, storedUtr)
        return storedSig == expectedSig
    }

    fun signInWithGoogle(email: String, displayName: String) {
        val cleanEmail = email.trim().lowercase()
        val cleanName = displayName.trim().ifEmpty { cleanEmail.substringBefore("@") }
        prefs.edit()
            .putString(KEY_GOOGLE_EMAIL, cleanEmail)
            .putString(KEY_GOOGLE_NAME, cleanName)
            .apply()
        _signedInEmail.value = cleanEmail
        _signedInName.value = cleanName
        _isOwnerAdmin.value = checkIsOwner(cleanEmail)
        _isVipUnlocked.value = verifyStoredVipState()
    }

    fun signOutGoogle() {
        prefs.edit()
            .remove(KEY_GOOGLE_EMAIL)
            .remove(KEY_GOOGLE_NAME)
            .apply()
        _signedInEmail.value = null
        _signedInName.value = null
        _isOwnerAdmin.value = false
        _isVipUnlocked.value = false
    }

    /**
     * Generates an official Owner Activation Key for a customer's 12-digit UTR.
     * ONLY available to the Owner (shahistatabassum9@gmail.com).
     */
    fun generateActivationKeyForUtr(utr12Digit: String): String {
        val cleanUtr = utr12Digit.trim()
        val digest = MessageDigest.getInstance("SHA-256")
            .digest("ALTINO_KING_VIP_2026:$cleanUtr".toByteArray())
        return digest.take(4).joinToString("") { "%02X".format(it) } // 8-char hex key e.g. "A4F91C8B"
    }

    /**
     * Verifies a 12-digit UTR + Activation Key and unlocks VIP only if cryptographically valid.
     * Prevents any free-unlock payment glitch!
     */
    fun verifyAndUnlockWithCode(utr12Digit: String, activationKey: String, plan: String): Boolean {
        val cleanUtr = utr12Digit.trim()
        if (cleanUtr.length != 12 || !cleanUtr.all { it.isDigit() }) {
            return false
        }
        val expectedKey = generateActivationKeyForUtr(cleanUtr)
        if (!activationKey.trim().equals(expectedKey, ignoreCase = true)) {
            return false
        }
        recordVerifiedUnlock(cleanUtr, plan)
        return true
    }

    /**
     * Unlocks VIP when a real Android UPI app Intent returns Status=SUCCESS and a valid approval reference.
     */
    fun verifyUpiIntentResponseAndUnlock(upiResponseString: String?, plan: String): Boolean {
        if (upiResponseString.isNullOrBlank()) return false
        val params = upiResponseString.split("&").associate { part ->
            val kv = part.split("=", limit = 2)
            val k = kv.getOrElse(0) { "" }.trim().lowercase()
            val v = kv.getOrElse(1) { "" }.trim()
            k to v
        }
        val status = params["status"]?.lowercase() ?: ""
        val approvalRef = params["approvalrefno"].takeUnless { it.isNullOrBlank() }
            ?: params["txnref"].takeUnless { it.isNullOrBlank() }
            ?: params["txnid"].takeUnless { it.isNullOrBlank() }

        if (status == "success" && !approvalRef.isNullOrBlank() && approvalRef.length >= 6) {
            recordVerifiedUnlock(approvalRef, plan)
            return true
        }
        return false
    }

    /**
     * Owner-only direct VIP activation for testing/admin verification.
     */
    fun unlockForOwnerAdmin(plan: String = PLAN_YEARLY): Boolean {
        if (!_isOwnerAdmin.value) return false
        recordVerifiedUnlock("999999999999", plan)
        return true
    }

    private fun recordVerifiedUnlock(utr: String, plan: String) {
        val email = _signedInEmail.value ?: ""
        val sig = computeSignature(email, utr)
        prefs.edit()
            .putBoolean(KEY_VIP_UNLOCKED, true)
            .putString(KEY_ACTIVE_PLAN, plan)
            .putString(KEY_VERIFIED_UTR, utr)
            .putString(KEY_VERIFIED_SIG, sig)
            .putLong(KEY_UNLOCKED_AT, System.currentTimeMillis())
            .apply()
        _isVipUnlocked.value = true
        _activePlan.value = plan
    }

    fun lockVip() {
        prefs.edit()
            .putBoolean(KEY_VIP_UNLOCKED, false)
            .remove(KEY_VERIFIED_UTR)
            .remove(KEY_VERIFIED_SIG)
            .putString(KEY_ACTIVE_PLAN, "NONE")
            .apply()
        _isVipUnlocked.value = false
        _activePlan.value = "NONE"
    }

    /**
     * Strictly locked to Owner (shahistatabassum9@gmail.com).
     * Copies the picked QR image to internal filesDir so it persists forever.
     */
    fun savePaymentQrFromUri(sourceUri: Uri): Boolean {
        if (!_isOwnerAdmin.value) return false
        return try {
            val destFile = File(appContext.filesDir, "owner_locked_payment_qr.png")
            appContext.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            val savedPath = destFile.absolutePath
            prefs.edit().putString(KEY_PAYMENT_QR_URI, savedPath).apply()
            _paymentQrUri.value = savedPath
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Strictly locked to Owner (shahistatabassum9@gmail.com).
     */
    fun saveOwnerPaymentDetails(upi: String, uid: String): Boolean {
        if (!_isOwnerAdmin.value) return false
        val cleanUpi = upi.trim()
        val cleanUid = uid.trim()
        if (cleanUpi.isNotEmpty()) {
            prefs.edit().putString(KEY_CREATOR_UPI, cleanUpi).apply()
            _creatorUpi.value = cleanUpi
        }
        if (cleanUid.isNotEmpty()) {
            prefs.edit().putString(KEY_CREATOR_UID, cleanUid).apply()
            _creatorUid.value = cleanUid
        }
        return true
    }

    private fun computeSignature(email: String, utr: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest("VIP_SIG_V2:${email.lowercase()}:$utr".toByteArray())
        return digest.take(8).joinToString("") { "%02x".format(it) }
    }

    companion object {
        const val OWNER_ADMIN_EMAIL = "shahistatabassum9@gmail.com"
        const val DEFAULT_OWNER_UPI = "notaltino.pvp@upi"
        const val DEFAULT_OWNER_UID = "UID-ALTINO-KING-01"

        const val PLAN_MONTHLY = "MONTHLY_50" // ₹50 / Month
        const val PLAN_YEARLY = "YEARLY_200"  // ₹200 / Year

        const val PRICE_MONTHLY_RUPEES = 50
        const val PRICE_YEARLY_RUPEES = 200

        private const val KEY_GOOGLE_EMAIL = "key_google_email"
        private const val KEY_GOOGLE_NAME = "key_google_name"
        private const val KEY_VIP_UNLOCKED = "key_vip_unlocked_v2"
        private const val KEY_ACTIVE_PLAN = "key_active_plan_v2"
        private const val KEY_VERIFIED_UTR = "key_verified_utr_v2"
        private const val KEY_VERIFIED_SIG = "key_verified_sig_v2"
        private const val KEY_UNLOCKED_AT = "key_unlocked_at_v2"
        private const val KEY_PAYMENT_QR_URI = "key_payment_qr_uri_v2"
        private const val KEY_CREATOR_UPI = "key_creator_upi_v2"
        private const val KEY_CREATOR_UID = "key_creator_uid_v2"

        @Volatile
        private var instance: VipManager? = null

        fun getInstance(context: Context): VipManager {
            return instance ?: synchronized(this) {
                instance ?: VipManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
