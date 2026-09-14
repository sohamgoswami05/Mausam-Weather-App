package com.imd.mausam2.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * WeatherHeroArt:
 * Renders a stylized 3D weather illustration featuring a glowing golden sun with
 * radiating sunbeams, overlapping fluffy cloud, and precipitation drops matching the
 * modern weather aesthetic.
 */
@Composable
fun WeatherHeroArt(
    weatherCode: Int,
    modifier: Modifier = Modifier.size(130.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "weatherArt")
    val sunPulse by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sunPulse"
    )
    val rainOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rainDrops"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Draw Glowing Radiant Sun (Positioned Upper Center-Right)
        val sunCenter = Offset(w * 0.58f, h * 0.36f)
        val sunRadius = (w * 0.22f) * sunPulse

        // Sun Glow Halo
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFD54F).copy(alpha = 0.45f),
                    Color(0xFFFFB300).copy(alpha = 0.2f),
                    Color.Transparent
                ),
                center = sunCenter,
                radius = sunRadius * 1.8f
            ),
            radius = sunRadius * 1.8f,
            center = sunCenter
        )

        // Radiating Sunbeam Spikes (12 rays)
        val rayCount = 12
        for (i in 0 until rayCount) {
            val angleDeg = (i * 360f / rayCount)
            val rad = Math.toRadians(angleDeg.toDouble())
            val rayStartDist = sunRadius * 1.18f
            val rayEndDist = sunRadius * 1.55f

            val startX = sunCenter.x + (rayStartDist * cos(rad)).toFloat()
            val startY = sunCenter.y + (rayStartDist * sin(rad)).toFloat()
            val endX = sunCenter.x + (rayEndDist * cos(rad)).toFloat()
            val endY = sunCenter.y + (rayEndDist * sin(rad)).toFloat()

            drawLine(
                color = Color(0xFFFFCA28),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = w * 0.032f,
                cap = StrokeCap.Round
            )
        }

        // Sun Core Disc (Golden Gradient)
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFFF176), Color(0xFFFFB300), Color(0xFFF57C00)),
                start = Offset(sunCenter.x - sunRadius, sunCenter.y - sunRadius),
                end = Offset(sunCenter.x + sunRadius, sunCenter.y + sunRadius)
            ),
            radius = sunRadius,
            center = sunCenter
        )

        // 2. Draw Soft Fluffy Cloud (Overlapping lower right quadrant)
        val cloudBaseY = h * 0.64f
        val cloudBaseX = w * 0.52f
        val cloudWidth = w * 0.44f
        val cloudHeight = h * 0.22f

        // Cloud Shadow / Depth Layer
        drawCloudShape(
            scope = this,
            centerX = cloudBaseX,
            centerY = cloudBaseY + 2f,
            width = cloudWidth,
            height = cloudHeight,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF94A3B8).copy(alpha = 0.35f), Color(0xFF64748B).copy(alpha = 0.2f))
            )
        )

        // Cloud Main Layer (Crisp Soft White-Grey Gradient)
        drawCloudShape(
            scope = this,
            centerX = cloudBaseX,
            centerY = cloudBaseY,
            width = cloudWidth,
            height = cloudHeight,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9), Color(0xFFCBD5E1))
            )
        )

        // 3. Precipitation Drops (3 Columns below cloud)
        val isRainy = weatherCode in listOf(51, 53, 55, 61, 63, 65, 80, 81, 82, 95, 96, 99) || weatherCode == 1 || weatherCode == 2 || weatherCode == 3
        if (isRainy) {
            val dropCols = listOf(
                Offset(cloudBaseX - cloudWidth * 0.3f, cloudBaseY + cloudHeight * 0.55f),
                Offset(cloudBaseX, cloudBaseY + cloudHeight * 0.6f),
                Offset(cloudBaseX + cloudWidth * 0.3f, cloudBaseY + cloudHeight * 0.55f)
            )

            dropCols.forEachIndexed { idx, colOrigin ->
                val stagger = (idx * 5f)
                val currentDropY = colOrigin.y + ((rainOffset + stagger) % 20f)
                drawRoundRect(
                    color = Color(0xFF93C5FD),
                    topLeft = Offset(colOrigin.x - 2f, currentDropY),
                    size = Size(4.5f, 9f),
                    cornerRadius = CornerRadius(2.5f, 2.5f)
                )

                // Secondary lighter drop below
                drawRoundRect(
                    color = Color(0xFFBFDBFE).copy(alpha = 0.6f),
                    topLeft = Offset(colOrigin.x - 2f, currentDropY + 12f),
                    size = Size(3.5f, 6.5f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
            }
        }
    }
}

private fun drawCloudShape(
    scope: DrawScope,
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float,
    brush: Brush
) {
    with(scope) {
        val path = Path().apply {
            val left = centerX - width / 2f
            val right = centerX + width / 2f
            val bottom = centerY + height / 2f

            // Bottom pill base
            val baseRect = RoundRect(
                left = left,
                top = bottom - height * 0.65f,
                right = right,
                bottom = bottom,
                cornerRadius = CornerRadius(height * 0.32f, height * 0.32f)
            )
            addRoundRect(baseRect)

            // Left puff circle
            addOval(
                androidx.compose.ui.geometry.Rect(
                    left = centerX - width * 0.42f,
                    top = centerY - height * 0.6f,
                    right = centerX - width * 0.05f,
                    bottom = bottom
                )
            )

            // Center large puff circle
            addOval(
                androidx.compose.ui.geometry.Rect(
                    left = centerX - width * 0.22f,
                    top = centerY - height * 0.95f,
                    right = centerX + width * 0.28f,
                    bottom = bottom
                )
            )

            // Right puff circle
            addOval(
                androidx.compose.ui.geometry.Rect(
                    left = centerX + width * 0.05f,
                    top = centerY - height * 0.5f,
                    right = centerX + width * 0.45f,
                    bottom = bottom
                )
            )
        }
        drawPath(path, brush = brush)
    }
}
