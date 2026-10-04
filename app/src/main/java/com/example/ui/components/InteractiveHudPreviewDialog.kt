package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ControlLayout
import com.example.model.HudButtonInfo
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MaceGold
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun InteractiveHudPreviewDialog(
    layout: ControlLayout,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var lastTappedAction by remember { mutableStateOf("Tap any button to test response & macros!") }
    var actionTriggerCount by remember { mutableStateOf(0) }

    fun triggerHaptic() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(35)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF090614)
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val screenW = maxWidth
                val screenH = maxHeight

                // Simulated Game World Sky & Ground
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF0B192C),
                                    Color(0xFF1E3E62),
                                    Color(0xFF1C160C),
                                    Color(0xFF0F0B06)
                                )
                            )
                        )
                )

                // Central Crosshair
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .width(16.dp)
                            .height(2.dp)
                            .background(Color.White.copy(alpha = 0.5f))
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .width(2.dp)
                            .height(16.dp)
                            .background(Color.White.copy(alpha = 0.5f))
                    )
                }

                // Render all touch buttons at their specified layout positions
                layout.previewButtons.forEach { btn ->
                    val btnLeft = screenW * btn.xPercent
                    val btnTop = screenH * btn.yPercent
                    val btnWidth = (screenW * btn.widthPercent).coerceAtLeast(36.dp)
                    val btnHeight = (screenH * btn.heightPercent).coerceAtLeast(36.dp)

                    if (btn.isJoystick) {
                        var thumbOffsetX by remember { mutableFloatStateOf(0f) }
                        var thumbOffsetY by remember { mutableFloatStateOf(0f) }
                        val stickSize = btnWidth.coerceAtLeast(btnHeight).coerceAtLeast(80.dp)

                        Box(
                            modifier = Modifier
                                .offset(x = btnLeft, y = btnTop)
                                .size(stickSize)
                                .clip(CircleShape)
                                .background(Color(0xFF082F49).copy(alpha = 0.65f))
                                .border(2.dp, Color(0xFF06B6D4), CircleShape)
                                .pointerInput(btn.id) {
                                    detectDragGestures(
                                        onDragStart = {
                                            triggerHaptic()
                                        },
                                        onDragEnd = {
                                            thumbOffsetX = 0f
                                            thumbOffsetY = 0f
                                            lastTappedAction = "🕹️ Joystick Centered (Idle)"
                                        },
                                        onDragCancel = {
                                            thumbOffsetX = 0f
                                            thumbOffsetY = 0f
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            val maxDist = 45f
                                            val newX = thumbOffsetX + dragAmount.x
                                            val newY = thumbOffsetY + dragAmount.y
                                            val dist = sqrt(newX * newX + newY * newY)
                                            if (dist > maxDist) {
                                                thumbOffsetX = (newX / dist) * maxDist
                                                thumbOffsetY = (newY / dist) * maxDist
                                            } else {
                                                thumbOffsetX = newX
                                                thumbOffsetY = newY
                                            }

                                            val angleRad = atan2(-thumbOffsetY, thumbOffsetX)
                                            var deg = Math.toDegrees(angleRad.toDouble()).toInt()
                                            if (deg < 0) deg += 360

                                            val direction = when (deg) {
                                                in 67..112 -> "FORWARD (Sprint W)"
                                                in 113..157 -> "FORWARD-LEFT (W+A)"
                                                in 158..202 -> "LEFT STRAFE (A)"
                                                in 203..247 -> "BACKWARD-LEFT (S+A)"
                                                in 248..292 -> "BACKWARD (S)"
                                                in 293..337 -> "BACKWARD-RIGHT (S+D)"
                                                in 23..66 -> "FORWARD-RIGHT (W+D)"
                                                else -> "RIGHT STRAFE (D)"
                                            }

                                            actionTriggerCount++
                                            lastTappedAction = "🕹️ 360° Joystick: $direction ($deg°)"
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Cardinal Direction Arrows
                            Text(
                                text = "▲",
                                color = Color(0xFF06B6D4).copy(alpha = 0.6f),
                                fontSize = 10.sp,
                                modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp)
                            )
                            Text(
                                text = "▼",
                                color = Color(0xFF06B6D4).copy(alpha = 0.6f),
                                fontSize = 10.sp,
                                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp)
                            )
                            Text(
                                text = "◀",
                                color = Color(0xFF06B6D4).copy(alpha = 0.6f),
                                fontSize = 10.sp,
                                modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)
                            )
                            Text(
                                text = "▶",
                                color = Color(0xFF06B6D4).copy(alpha = 0.6f),
                                fontSize = 10.sp,
                                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 4.dp)
                            )

                            // Inner Draggable Thumb Stick
                            Box(
                                modifier = Modifier
                                    .offset { IntOffset(thumbOffsetX.toInt(), thumbOffsetY.toInt()) }
                                    .size(stickSize * 0.45f)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(Color(0xFF22D3EE), Color(0xFF0891B2))
                                        )
                                    )
                                    .border(1.5.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "360°",
                                    color = Color.Black,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    } else {
                        val isSpecial = btn.isTwoRoles || btn.isSwipe
                        val color = if (isSpecial) {
                            if (btn.isTwoRoles) Color(0xFFF43F5E) else Color(0xFFFF66C4)
                        } else {
                            Color(btn.colorHex)
                        }

                        Box(
                            modifier = Modifier
                                .offset(x = btnLeft, y = btnTop)
                                .size(width = btnWidth, height = btnHeight)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color.copy(alpha = if (isSpecial) 0.85f else 0.5f))
                                .border(
                                    width = if (isSpecial) 2.5.dp else 1.dp,
                                    color = if (isSpecial) Color.White else color.copy(alpha = 0.9f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .pointerInput(btn.id) {
                                    detectTapGestures(
                                        onPress = {
                                            triggerHaptic()
                                            actionTriggerCount++
                                            lastTappedAction = if (btn.isTwoRoles) {
                                                "⚡ [1-BUTTON 2-ROLES TRIGGERED] ${btn.name}: ${btn.roleDesc}"
                                            } else if (btn.isSwipe) {
                                                "🌀 [SWIPE MACRO TRIGGERED] ${btn.name}: ${btn.roleDesc}"
                                            } else {
                                                "🎮 Tapped '${btn.name}' (Keycode: ${btn.keycodes.joinToString()})"
                                            }
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = btn.name,
                                color = Color.White,
                                fontSize = if (btn.name.length > 5) 10.sp else 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Top Floating Control Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                            .border(1.dp, NeonPurple, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Column {
                            Text(
                                text = "LIVE HUD SIMULATOR • ${layout.fileName}",
                                color = ElectricCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = layout.name,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.8f), CircleShape)
                            .border(1.dp, Color(0xFF47337A), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit Simulator",
                            tint = Color.White
                        )
                    }
                }

                // Bottom Action Feedback Bar
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.85f))
                        .border(1.5.dp, MaceGold, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = MaceGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = lastTappedAction,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
