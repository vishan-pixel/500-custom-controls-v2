package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HudButtonInfo
import kotlin.random.Random

@Composable
fun HudMiniPreview(
    buttons: List<HudButtonInfo>,
    modifier: Modifier = Modifier,
    categoryColor: Color = Color(0xFFA855F7),
    hasLightningEffect: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lightning_anim")
    val lightningPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Box(
        modifier = modifier
            .aspectRatio(16f / 9.5f)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0D0A1A))
            .border(
                1.dp,
                if (hasLightningEffect) {
                    Brush.linearGradient(
                        listOf(
                            Color(0xFFFFD700),
                            Color(0xFF22D3EE),
                            Color(0xFFFFD700)
                        )
                    )
                } else {
                    Brush.horizontalGradient(listOf(categoryColor.copy(alpha = 0.6f), Color(0xFF3B2D6B)))
                },
                RoundedCornerShape(10.dp)
            )
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(3.dp)) {
            val canvasW = size.width
            val canvasH = size.height

            drawRect(
                color = if (hasLightningEffect) Color(0xFF0B071E) else Color(0xFF140E26),
                size = size
            )

            if (hasLightningEffect) {
                val rand = Random((lightningPhase * 100).toInt())
                val path = Path()
                var currentX = canvasW * 0.15f
                var currentY = canvasH * 0.2f
                path.moveTo(currentX, currentY)
                for (step in 1..5) {
                    currentX += (canvasW * 0.15f) + (rand.nextFloat() * 10f - 5f)
                    currentY += (rand.nextFloat() * 20f - 10f)
                    path.lineTo(currentX, currentY.coerceIn(0f, canvasH))
                }
                drawPath(
                    path = path,
                    color = Color(0xFF22D3EE).copy(alpha = 0.7f),
                    style = Stroke(width = 2.5f)
                )
                drawPath(
                    path = path,
                    color = Color(0xFFFFD700).copy(alpha = 0.4f),
                    style = Stroke(width = 4.5f)
                )
            }

            // Draw buttons & 360° Finger Joystick
            buttons.forEach { btn ->
                val x = (btn.xPercent * canvasW).coerceIn(0f, canvasW - 6f)
                val y = (btn.yPercent * canvasH).coerceIn(0f, canvasH - 6f)
                val w = (btn.widthPercent * canvasW).coerceIn(5f, canvasW - x)
                val h = (btn.heightPercent * canvasH).coerceIn(5f, canvasH - y)

                if (btn.isJoystick) {
                    val stickRadius = (w.coerceAtMost(h) * 0.52f).coerceAtLeast(10f)
                    val stickCenter = Offset(x + stickRadius, y + stickRadius)
                    drawCircle(
                        color = if (hasLightningEffect) Color(0xFFFFD700).copy(alpha = 0.35f) else Color(0xFF06B6D4).copy(alpha = 0.35f),
                        radius = stickRadius,
                        center = stickCenter
                    )
                    drawCircle(
                        color = if (hasLightningEffect) Color(0xFFFFD700) else Color(0xFF22D3EE),
                        radius = stickRadius,
                        center = stickCenter,
                        style = Stroke(width = 2f)
                    )
                    drawCircle(
                        color = if (hasLightningEffect) Color(0xFF22D3EE).copy(alpha = 0.9f) else Color(0xFF06B6D4).copy(alpha = 0.9f),
                        radius = stickRadius * 0.44f,
                        center = stickCenter
                    )
                } else {
                    val btnColor = Color(btn.colorHex)
                    val radius = if (btn.cornerRadius >= 50f) 12f else 3f
                    drawRoundRect(
                        color = btnColor.copy(alpha = if (btn.isTwoRoles || btn.isSwipe) 0.72f else 0.42f),
                        topLeft = Offset(x, y),
                        size = Size(w, h),
                        cornerRadius = CornerRadius(radius, radius)
                    )
                    drawRoundRect(
                        color = if (btn.isSwipe) Color(0xFFFFD700) else btnColor.copy(alpha = 0.95f),
                        topLeft = Offset(x, y),
                        size = Size(w, h),
                        cornerRadius = CornerRadius(radius, radius),
                        style = Stroke(width = if (btn.isTwoRoles || btn.isSwipe) 1.6f else 0.9f)
                    )
                }
            }
        }

        // Top-right button count & v9 badge
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .background(Color.Black.copy(alpha = 0.78f), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (hasLightningEffect) "⚡ ${buttons.size} BTNS v9" else "🕹️ ${buttons.size} BTNS v9",
                color = if (hasLightningEffect) Color(0xFFFFD700) else categoryColor,
                fontSize = 8.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun HudFullDiagram(
    buttons: List<HudButtonInfo>,
    modifier: Modifier = Modifier,
    categoryColor: Color = Color(0xFFA855F7),
    hasLightningEffect: Boolean = false,
    selectedButtonId: String? = null,
    onButtonSelect: ((HudButtonInfo) -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "full_lightning_anim")
    val lightningPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF090614))
            .border(
                1.5.dp,
                if (hasLightningEffect) {
                    Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFF22D3EE), Color(0xFFA855F7)))
                } else {
                    Brush.linearGradient(listOf(categoryColor, Color(0xFF3B2D6B)))
                },
                RoundedCornerShape(14.dp)
            )
    ) {
        val boxW = maxWidth
        val boxH = maxHeight

        Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
            val canvasW = size.width
            val canvasH = size.height

            drawRoundRect(
                color = if (hasLightningEffect) Color(0xFF0C0721) else Color(0xFF130D29),
                size = size,
                cornerRadius = CornerRadius(10f, 10f)
            )

            if (hasLightningEffect) {
                val rand = Random((lightningPhase * 150).toInt())
                for (branch in 1..2) {
                    val path = Path()
                    var startX = if (branch == 1) canvasW * 0.2f else canvasW * 0.6f
                    var startY = if (branch == 1) canvasH * 0.3f else canvasH * 0.6f
                    path.moveTo(startX, startY)
                    for (seg in 1..4) {
                        startX += (rand.nextFloat() * 40f - 20f)
                        startY += (rand.nextFloat() * 30f - 15f)
                        path.lineTo(startX.coerceIn(10f, canvasW - 10f), startY.coerceIn(10f, canvasH - 10f))
                    }
                    drawPath(path, color = Color(0xFF22D3EE).copy(alpha = 0.8f), style = Stroke(width = 2.5f))
                    drawPath(path, color = Color(0xFFFFD700).copy(alpha = 0.4f), style = Stroke(width = 5f))
                }
            }

            // Center Crosshair
            val cx = canvasW * 0.5f
            val cy = canvasH * 0.48f
            drawLine(Color.White.copy(alpha = 0.35f), Offset(cx - 8f, cy), Offset(cx + 8f, cy), strokeWidth = 2f)
            drawLine(Color.White.copy(alpha = 0.35f), Offset(cx, cy - 8f), Offset(cx, cy + 8f), strokeWidth = 2f)

            buttons.forEach { btn ->
                val x = (btn.xPercent * canvasW).coerceIn(0f, canvasW - 10f)
                val y = (btn.yPercent * canvasH).coerceIn(0f, canvasH - 10f)
                val w = (btn.widthPercent * canvasW).coerceIn(10f, canvasW - x)
                val h = (btn.heightPercent * canvasH).coerceIn(10f, canvasH - y)

                val isSelected = btn.id == selectedButtonId
                val btnColor = Color(btn.colorHex)

                if (btn.isJoystick) {
                    val stickRadius = (w.coerceAtMost(h) * 0.52f).coerceAtLeast(16f)
                    val stickCenter = Offset(x + stickRadius, y + stickRadius)
                    drawCircle(
                        color = (if (hasLightningEffect) Color(0xFFFFD700) else Color(0xFF06B6D4)).copy(alpha = 0.35f),
                        radius = stickRadius,
                        center = stickCenter
                    )
                    drawCircle(
                        color = if (isSelected) Color.White else (if (hasLightningEffect) Color(0xFFFFD700) else Color(0xFF22D3EE)),
                        radius = stickRadius,
                        center = stickCenter,
                        style = Stroke(width = 2.5f)
                    )
                    drawCircle(
                        color = (if (hasLightningEffect) Color(0xFF22D3EE) else Color(0xFF06B6D4)).copy(alpha = 0.9f),
                        radius = stickRadius * 0.44f,
                        center = stickCenter
                    )
                } else {
                    val cRad = if (btn.cornerRadius >= 50f) 18f else 5f
                    drawRoundRect(
                        color = btnColor.copy(alpha = if (btn.isTwoRoles || btn.isSwipe || isSelected) 0.65f else 0.38f),
                        topLeft = Offset(x, y),
                        size = Size(w, h),
                        cornerRadius = CornerRadius(cRad, cRad)
                    )
                    drawRoundRect(
                        color = if (isSelected) Color.White else btnColor,
                        topLeft = Offset(x, y),
                        size = Size(w, h),
                        cornerRadius = CornerRadius(cRad, cRad),
                        style = Stroke(width = if (btn.isSwipe || btn.isTwoRoles) 2f else 1.2f)
                    )
                }
            }
        }

        // Render Button Labels Over the HUD Diagram!
        buttons.forEach { btn ->
            val offsetX = boxW * btn.xPercent.coerceIn(0.0f, 0.92f)
            val offsetY = boxH * btn.yPercent.coerceIn(0.0f, 0.90f)
            val wDp = boxW * btn.widthPercent.coerceIn(0.04f, 0.2f)
            val hDp = boxH * btn.heightPercent.coerceIn(0.05f, 0.25f)

            Box(
                modifier = Modifier
                    .offset(x = offsetX, y = offsetY)
                    .size(width = wDp, height = hDp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (btn.isJoystick) "🕹️JOY" else btn.name,
                    color = Color.White,
                    fontSize = 7.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
            }
        }
    }
}
