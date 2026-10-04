package com.example.ui.components

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.VipManager
import java.io.File

@Composable
fun VipSubscriptionDialog(
    vipManager: VipManager,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isVipUnlocked by vipManager.isVipUnlocked.collectAsState()
    val isOwnerAdmin by vipManager.isOwnerAdmin.collectAsState()
    val signedInEmail by vipManager.signedInEmail.collectAsState()
    val paymentQrUri by vipManager.paymentQrUri.collectAsState()
    val creatorUpi by vipManager.creatorUpi.collectAsState()
    val creatorUid by vipManager.creatorUid.collectAsState()

    var selectedPlan by remember { mutableStateOf(VipManager.PLAN_YEARLY) }
    var showEditOwnerDialog by remember { mutableStateOf(false) }
    var tempUpi by remember(creatorUpi) { mutableStateOf(creatorUpi) }
    var tempUid by remember(creatorUid) { mutableStateOf(creatorUid) }

    // Customer Payment Verification Inputs (Anti-Glitch)
    var utrInput by remember { mutableStateOf("") }
    var activationKeyInput by remember { mutableStateOf("") }
    var verificationError by remember { mutableStateOf<String?>(null) }

    // Owner Key Generator State
    var ownerCustomerUtr by remember { mutableStateOf("") }
    var generatedCustomerKey by remember { mutableStateOf<String?>(null) }

    var qrBitmap by remember(paymentQrUri) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(paymentQrUri) {
        val pathOrUri = paymentQrUri
        if (pathOrUri != null) {
            try {
                val file = File(pathOrUri)
                if (file.exists()) {
                    val bmp = BitmapFactory.decodeFile(file.absolutePath)
                    qrBitmap = bmp?.asImageBitmap()
                } else {
                    val uri = Uri.parse(pathOrUri)
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        val bmp = BitmapFactory.decodeStream(stream)
                        qrBitmap = bmp?.asImageBitmap()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                qrBitmap = null
            }
        } else {
            qrBitmap = null
        }
    }

    // Owner-Only Photo Picker to upload/lock QR code
    val pickQrLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            if (!isOwnerAdmin) {
                Toast.makeText(context, "Access Denied: Only the App Owner can change the QR Code!", Toast.LENGTH_LONG).show()
                return@rememberLauncherForActivityResult
            }
            val ok = vipManager.savePaymentQrFromUri(uri)
            if (ok) {
                Toast.makeText(context, "👑 Owner Payment QR Code locked & saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to save QR image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Real UPI Intent Result Verifier (Prevents fake/cancelled payment glitch)
    val upiPaymentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val dataStr = result.data?.getStringExtra("response")
        if (result.resultCode == Activity.RESULT_OK && vipManager.verifyUpiIntentResponseAndUnlock(dataStr, selectedPlan)) {
            verificationError = null
            Toast.makeText(context, "✅ UPI Payment Verified! VIP God Controls Unlocked!", Toast.LENGTH_LONG).show()
        } else {
            verificationError = "❌ UPI Payment was not completed or verified. Please complete payment or enter your 12-digit UTR + Activation Key below."
            Toast.makeText(context, "Payment not completed. VIP remains locked.", Toast.LENGTH_SHORT).show()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "vip_pulse")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0xFFFFD700).copy(alpha = borderAlpha),
                            Color(0xFF22D3EE).copy(alpha = borderAlpha),
                            Color(0xFFA855F7).copy(alpha = borderAlpha)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .testTag("vip_subscription_dialog"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0B1E)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFFFD700).copy(alpha = 0.2f), CircleShape)
                                .border(1.5.dp, Color(0xFFFFD700), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👑", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "VIP GOD CONTROLS ROOM",
                                color = Color(0xFFFFD700),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Signed in: ${signedInEmail ?: "Guest"}",
                                color = Color(0xFF22D3EE),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF1E1538), CircleShape)
                            .testTag("vip_dialog_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // VIP Status Banner
                if (isVipUnlocked) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF064E3B), RoundedCornerShape(16.dp))
                            .border(1.5.dp, Color(0xFF10B981), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "👑 VERIFIED VIP MEMBERSHIP ACTIVE!",
                                    color = Color(0xFF6EE7B7),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "All 55 Super Duper Crazy VIP God v9 Controls (41+ Buttons + 360° Finger Joystick) are unlocked!",
                                    color = Color(0xFFD1FAE5),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Plan Selector
                Text(
                    text = "SELECT YOUR VIP SUBSCRIPTION PLAN",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Monthly Plan (₹50)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPlan = VipManager.PLAN_MONTHLY }
                            .border(
                                width = if (selectedPlan == VipManager.PLAN_MONTHLY) 2.dp else 1.dp,
                                color = if (selectedPlan == VipManager.PLAN_MONTHLY) Color(0xFF22D3EE) else Color(0xFF2A1E4A),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("plan_monthly"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedPlan == VipManager.PLAN_MONTHLY) Color(0xFF132238) else Color(0xFF130E26)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("MONTHLY", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("₹50", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                            Text("/ month", color = Color(0xFF22D3EE), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("55 VIP God Controls", color = Color(0xFF94A3B8), fontSize = 10.sp, textAlign = TextAlign.Center)
                        }
                    }

                    // Yearly Plan (₹200)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPlan = VipManager.PLAN_YEARLY }
                            .border(
                                width = if (selectedPlan == VipManager.PLAN_YEARLY) 2.dp else 1.dp,
                                color = if (selectedPlan == VipManager.PLAN_YEARLY) Color(0xFFFFD700) else Color(0xFF2A1E4A),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("plan_yearly"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedPlan == VipManager.PLAN_YEARLY) Color(0xFF281F0B) else Color(0xFF130E26)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFFD700), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("SAVE 67% 🔥", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("YEARLY", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("₹200", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                            Text("/ year", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // LOCKED PAYMENT QR & UID CARD (Only Owner shahistatabassum9@gmail.com can modify!)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, Color(0xFF33245E), RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF130E26)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "OFFICIAL OWNER PAYMENT QR",
                                    color = Color(0xFFFFD700),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            // STRICT OWNER CHECK: Only shahistatabassum9@gmail.com sees the Upload QR button!
                            if (isOwnerAdmin) {
                                Button(
                                    onClick = {
                                        pickQrLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(30.dp)
                                        .testTag("upload_qr_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Upload,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (paymentQrUri != null) "Owner: Change QR" else "Owner: Upload QR",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF064E3B), RoundedCornerShape(6.dp))
                                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "🔒 OWNER LOCKED",
                                        color = Color(0xFF6EE7B7),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // QR Display Box
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .border(2.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp))
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (qrBitmap != null) {
                                Image(
                                    bitmap = qrBitmap!!,
                                    contentDescription = "Locked Owner Payment QR Code",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode2,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(95.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "SCAN TO PAY OWNER UPI",
                                        color = Color(0xFF0F172A),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = creatorUpi,
                                        color = Color(0xFF475569),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Locked UPI ID & UID Info Box (Read-Only for everyone except Owner!)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E1538), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "LOCKED OWNER UPI ID",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = creatorUpi,
                                        color = Color(0xFF22D3EE),
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "MERCHANT UID: $creatorUid",
                                        color = Color(0xFFFFD700),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Owner UPI ID", creatorUpi)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Copied Owner UPI ID: $creatorUpi", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy UPI ID",
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // STRICT OWNER CHECK: Only Owner can edit UPI or UID!
                                    if (isOwnerAdmin) {
                                        IconButton(
                                            onClick = { showEditOwnerDialog = true },
                                            modifier = Modifier.testTag("edit_owner_upi_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Owner Edit UPI & UID",
                                                tint = Color(0xFF34D399),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val amount = if (selectedPlan == VipManager.PLAN_YEARLY) 200 else 50

                // Pay via UPI App (Result-verified)
                Button(
                    onClick = {
                        try {
                            val trRef = "VIP${System.currentTimeMillis()}"
                            val upiUri = Uri.parse(
                                "upi://pay?pa=$creatorUpi&pn=NotAltinoKingControls&tid=$creatorUid&tr=$trRef&am=$amount&cu=INR&tn=VIP_God_Controls"
                            )
                            val intent = Intent(Intent.ACTION_VIEW, upiUri)
                            upiPaymentLauncher.launch(intent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "No UPI app installed. Scan QR code or pay to $creatorUpi and verify UTR below.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("pay_upi_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Step 1: Pay ₹$amount via UPI App",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ANTI-GLITCH PAYMENT VERIFICATION CARD (No Free Unlock!)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF3B2D6B), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF150E2B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🔐 STEP 2: VERIFY PAYMENT TO UNLOCK VIP",
                            color = Color(0xFFFFD700),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "After paying ₹$amount, enter your 12-digit UPI UTR Ref No. and the Verified Activation Key from Owner (Discord: Not_Altino) to unlock:",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = utrInput,
                            onValueChange = {
                                utrInput = it.filter { ch -> ch.isDigit() }.take(12)
                                verificationError = null
                            },
                            label = { Text("12-Digit UPI UTR / Ref Number") },
                            placeholder = { Text("e.g. 426819045128") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("utr_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF22D3EE),
                                unfocusedBorderColor = Color(0xFF33245E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = activationKeyInput,
                            onValueChange = {
                                activationKeyInput = it.uppercase().take(12)
                                verificationError = null
                            },
                            label = { Text("Verified Activation Key") },
                            placeholder = { Text("8-Character Owner Key") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("activation_key_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color(0xFF33245E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        if (verificationError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = verificationError!!,
                                color = Color(0xFFEF4444),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val unlocked = vipManager.verifyAndUnlockWithCode(
                                    utr12Digit = utrInput,
                                    activationKey = activationKeyInput,
                                    plan = selectedPlan
                                )
                                if (unlocked) {
                                    verificationError = null
                                    Toast.makeText(context, "👑 Payment Verified! VIP God Controls Unlocked!", Toast.LENGTH_LONG).show()
                                } else {
                                    verificationError = "❌ Verification Failed! Invalid 12-digit UTR or Activation Key. Cannot unlock VIP without verified payment."
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("verify_payment_unlock_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Verify Payment & Unlock VIP",
                                color = Color.Black,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                // STRICT OWNER-ONLY ADMIN PANEL (Visible ONLY to shahistatabassum9@gmail.com)
                if (isOwnerAdmin) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, Color(0xFF10B981), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF062E22)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "👑 OWNER ADMIN PANEL (${VipManager.OWNER_ADMIN_EMAIL})",
                                color = Color(0xFF34D399),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Only you can see this panel. Generate Activation Keys for customers who paid via QR, or toggle Owner VIP access:",
                                color = Color(0xFFD1FAE5),
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = ownerCustomerUtr,
                                onValueChange = { ownerCustomerUtr = it.filter { c -> c.isDigit() }.take(12) },
                                label = { Text("Enter Customer's 12-Digit UTR") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF34D399),
                                    unfocusedBorderColor = Color(0xFF10B981),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        if (ownerCustomerUtr.length == 12) {
                                            val key = vipManager.generateActivationKeyForUtr(ownerCustomerUtr)
                                            generatedCustomerKey = key
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("VIP Activation Key", key))
                                            Toast.makeText(context, "Copied Customer Key: $key", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Enter a 12-digit UTR first", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                ) {
                                    Text("Generate Key", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }

                                Button(
                                    onClick = {
                                        if (isVipUnlocked) {
                                            vipManager.lockVip()
                                            Toast.makeText(context, "VIP Locked", Toast.LENGTH_SHORT).show()
                                        } else {
                                            vipManager.unlockForOwnerAdmin(selectedPlan)
                                            Toast.makeText(context, "👑 Owner VIP Activated!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                                ) {
                                    Text(
                                        text = if (isVipUnlocked) "Lock VIP" else "Owner Unlock",
                                        color = Color.Black,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            if (generatedCustomerKey != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Generated Activation Key: $generatedCustomerKey (Copied!)",
                                    color = Color(0xFFFFD700),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Owner-Only Edit UPI & UID Dialog
    if (showEditOwnerDialog && isOwnerAdmin) {
        AlertDialog(
            onDismissRequest = { showEditOwnerDialog = false },
            title = {
                Text(
                    text = "👑 Owner: Update Locked UPI & UID",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Only you (${VipManager.OWNER_ADMIN_EMAIL}) can change these payment credentials:",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = tempUpi,
                        onValueChange = { tempUpi = it },
                        label = { Text("Creator UPI ID") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_upi_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD700),
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempUid,
                        onValueChange = { tempUid = it },
                        label = { Text("Merchant / Creator UID") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_uid_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF22D3EE),
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vipManager.saveOwnerPaymentDetails(tempUpi, tempUid)
                        showEditOwnerDialog = false
                        Toast.makeText(context, "👑 Locked UPI & UID updated!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text("Save & Lock", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditOwnerDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF1E1538),
            shape = RoundedCornerShape(16.dp)
        )
    }
}
