package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LayoutJsonGenerator
import com.example.model.LayoutCategory
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CrystalMagenta
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MaceGold
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UploadCustomLayoutDialog(
    onDismiss: () -> Unit,
    onSaveCustomLayout: (name: String, fileName: String, category: String, description: String, json: String) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var layoutName by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(LayoutCategory.CRYSTAL) }
    var description by remember { mutableStateOf("") }
    var jsonContent by remember { mutableStateOf("") }

    // File Picker to select .json file directly from phone storage
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val content = inputStream?.bufferedReader()?.use { it.readText() } ?: ""
                jsonContent = content

                // Extract filename from Uri or fallback
                val pathSegment = uri.lastPathSegment ?: "custom_layout.json"
                val extractedName = if (pathSegment.contains("/")) pathSegment.substringAfterLast("/") else pathSegment
                val cleanFileName = if (extractedName.endsWith(".json")) extractedName else "$extractedName.json"
                if (fileName.isBlank()) {
                    fileName = cleanFileName
                }
                if (layoutName.isBlank()) {
                    layoutName = cleanFileName.removeSuffix(".json").replace("_", " ").replaceFirstChar { it.uppercase() }
                }
                if (description.isBlank()) {
                    description = "Custom layout imported from phone storage."
                }
                Toast.makeText(context, "Loaded $cleanFileName from storage!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to read file: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DeepViolet,
        scrimColor = Color.Black.copy(alpha = 0.75f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF10B981).copy(alpha = 0.2f), CircleShape)
                            .border(1.dp, Color(0xFF10B981), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "UPLOAD MY OWN CONTROL",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Permanent Local Offline Storage",
                            color = MaceGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Import from Phone Storage Action Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Select .json from Storage",
                            color = ElectricCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Import layouts directly from Pojav, Mojo, or Downloads folder",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    Button(
                        onClick = {
                            filePickerLauncher.launch("*/*")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileOpen,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Browse",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Layout Name
            Text(
                text = "LAYOUT NAME",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = layoutName,
                onValueChange = { layoutName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. My God Tier Crystal 4-Finger", color = TextMuted, fontSize = 12.sp) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = CardBorder,
                    focusedContainerColor = CardDark,
                    unfocusedContainerColor = CardDark,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // File Name
            Text(
                text = "FILE NAME (.json)",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = fileName,
                onValueChange = { fileName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. my_crystal_pro.json", color = TextMuted, fontSize = 12.sp) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = CardBorder,
                    focusedContainerColor = CardDark,
                    unfocusedContainerColor = CardDark,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Selector
            Text(
                text = "CATEGORY",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val availableCategories = listOf(
                    LayoutCategory.CRYSTAL,
                    LayoutCategory.JOYSTICK,
                    LayoutCategory.MACE,
                    LayoutCategory.ANCHOR,
                    LayoutCategory.CART,
                    LayoutCategory.MACRO_SWIPE,
                    LayoutCategory.SWORD_AXE,
                    LayoutCategory.YOUTUBER
                )
                availableCategories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    val catColor = Color(cat.colorHex)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) catColor.copy(alpha = 0.3f) else CardDark)
                            .border(1.dp, if (isSelected) catColor else CardBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "${cat.iconEmoji} ${cat.displayName}",
                            color = if (isSelected) catColor else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Description / Setup Advice
            Text(
                text = "SETUP NOTES & MACRO GUIDE",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Explain button roles, macro keys, recommended grip, etc.", color = TextMuted, fontSize = 12.sp) },
                maxLines = 3,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = CardBorder,
                    focusedContainerColor = CardDark,
                    unfocusedContainerColor = CardDark,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // JSON Content Header with Paste / Auto-Template Helpers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CONTROLMAP JSON CODE",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Paste from clipboard
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipText = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                            if (clipText.isNotBlank()) {
                                jsonContent = clipText
                                Toast.makeText(context, "Pasted JSON from clipboard!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = ElectricCyan
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Paste", fontSize = 10.sp, color = ElectricCyan)
                    }

                    // Auto generate starter template
                    OutlinedButton(
                        onClick = {
                            val autoName = if (layoutName.isNotBlank()) layoutName else "My Custom Setup"
                            val autoFile = if (fileName.isNotBlank()) fileName else "custom_control.json"
                            jsonContent = LayoutJsonGenerator.generatePojavControlJson(
                                layoutName = autoName,
                                fileName = autoFile,
                                category = selectedCategory.displayName,
                                buttons = emptyList(),
                                macroInfo = description
                            )
                            Toast.makeText(context, "Generated valid Pojav starter JSON!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaceGold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Starter Template", fontSize = 10.sp, color = MaceGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = jsonContent,
                onValueChange = { jsonContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                placeholder = {
                    Text(
                        "Paste PojavLauncher/Mojo JSON here, or click 'Select .json from Storage' above...",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                shape = RoundedCornerShape(10.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFFA5B4FC)
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = CardBorder,
                    focusedContainerColor = Color(0xFF0A0717),
                    unfocusedContainerColor = Color(0xFF0A0717),
                    focusedTextColor = Color(0xFFA5B4FC),
                    unfocusedTextColor = Color(0xFFA5B4FC)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Save Action Button
            Button(
                onClick = {
                    val finalName = layoutName.ifBlank { "My Custom Layout" }
                    val finalFile = if (fileName.isBlank()) {
                        "custom_${System.currentTimeMillis() % 1000}.json"
                    } else if (fileName.endsWith(".json")) {
                        fileName
                    } else {
                        "$fileName.json"
                    }
                    val finalJson = jsonContent.ifBlank {
                        LayoutJsonGenerator.generatePojavControlJson(
                            layoutName = finalName,
                            fileName = finalFile,
                            category = selectedCategory.displayName,
                            buttons = emptyList(),
                            macroInfo = description
                        )
                    }
                    val finalDesc = description.ifBlank { "User uploaded custom control layout." }

                    onSaveCustomLayout(
                        finalName,
                        finalFile,
                        selectedCategory.id,
                        finalDesc,
                        finalJson
                    )
                    Toast.makeText(context, "Saved $finalFile to My Controls! Offline ready.", Toast.LENGTH_LONG).show()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save to My Controls (Offline Ready)",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
