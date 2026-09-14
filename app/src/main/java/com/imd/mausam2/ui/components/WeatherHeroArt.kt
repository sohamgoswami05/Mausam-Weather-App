package com.imd.mausam2.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * WeatherHeroArt:
 * Authentic celestial weather illustration featuring:
 * - Daytime: Radiant 3D golden sun with wide ethereal celestial halo and tapered rays.
 * - Nighttime: Luminous silver moon with ethereal silver lunar halo and ambient twinkling stars.
 * - Rain/Storm: Ambient blue mist and electric violet storm halos with animated precipitation.
 */
@Composable
fun WeatherHeroArt(
    weatherCode: Int,
    modifier: Modifier = Modifier.size(135.dp)
) {
    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val isDay = currentHour in 6..18

    val isRainy = weatherCode in listOf(51, 53, 55, 61, 63, 65, 80, 81, 82, 95, 96, 99) || weatherCode in listOf(1, 2, 3)
    val isThunderstorm = weatherCode in listOf(95, 96, 99)

    val infiniteTransition = rememberInfiniteTransition(label = "weatherArt")

    // Breathing Celestial Halo Animation
    val haloPulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloPulse"
    )

    // Twinkle / Lightning Flash Animation
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isThunderstorm) 400 else 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )

    // Raindrop Fall Animation
    val rainOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rainDrops"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val celestialCenter = Offset(w * 0.58f, h * 0.38f)
        val celestialRadius = w * 0.22f

        if (isDay) {
            // ==========================================
            // 1. DAYTIME: SUN WITH ETHEREAL CELESTIAL HALO
            // ==========================================

            // A. Outer Ethereal Golden Atmospheric Halo (Spreads softly into sky)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFD54F).copy(alpha = 0.40f * haloPulse),
                        Color(0xFFFFB300).copy(alpha = 0.22f * haloPulse),
                        Color(0xFFFFA000).copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = celestialCenter,
                    radius = celestialRadius * 2.4f
                ),
                radius = celestialRadius * 2.4f,
                center = celestialCenter
            )

            // B. Intermediate Luminous Golden Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF9C4).copy(alpha = 0.65f),
                        Color(0xFFFFE082).copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = celestialCenter,
                    radius = celestialRadius * 1.55f
                ),
                radius = celestialRadius * 1.55f,
                center = celestialCenter
            )

            // C. 12 Radiating Tapered Sunbeams
            val rayCount = 12
            for (i in 0 until rayCount) {
                val angleDeg = (i * 360f / rayCount)
                val rad = Math.toRadians(angleDeg.toDouble())
                val startDist = celestialRadius * 1.14f
                val endDist = celestialRadius * 1.58f

                val startX = celestialCenter.x + (startDist * cos(rad)).toFloat()
                val startY = celestialCenter.y + (startDist * sin(rad)).toFloat()
                val endX = celestialCenter.x + (endDist * cos(rad)).toFloat()
                val endY = celestialCenter.y + (endDist * sin(rad)).toFloat()

                drawLine(
                    color = Color(0xFFFFD54F),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = w * 0.030f,
                    cap = StrokeCap.Round
                )
            }

            // D. 3D Spherical Sun Body (Offset Highlight toward top-left)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFFDE7), // Specular highlight
                        Color(0xFFFFEE58), // Bright gold
                        Color(0xFFFFB300), // Rich amber
                        Color(0xFFE65100)  // Deep shadow rim
                    ),
                    center = Offset(celestialCenter.x - celestialRadius * 0.25f, celestialCenter.y - celestialRadius * 0.25f),
                    radius = celestialRadius * 1.15f
                ),
                radius = celestialRadius,
                center = celestialCenter
            )
        } else {
            // ==========================================
            // 2. NIGHTTIME: MOON WITH SILVER LUNAR HALO
            // ==========================================

            // A. Outer Ethereal Silver Lunar Halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE2E8F0).copy(alpha = 0.38f * haloPulse),
                        Color(0xFFCBD5E1).copy(alpha = 0.20f * haloPulse),
                        Color(0xFF94A3B8).copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    center = celestialCenter,
                    radius = celestialRadius * 2.3f
                ),
                radius = celestialRadius * 2.3f,
                center = celestialCenter
            )

            // B. Intermediate Silver Moon Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF).copy(alpha = 0.60f),
                        Color(0xFFF1F5F9).copy(alpha = 0.30f),
                        Color.Transparent
                    ),
                    center = celestialCenter,
                    radius = celestialRadius * 1.45f
                ),
                radius = celestialRadius * 1.45f,
                center = celestialCenter
            )

            // C. Twinkling Night Stars in Sky
            val starOffsets = listOf(
                Offset(w * 0.15f, h * 0.22f),
                Offset(w * 0.32f, h * 0.15f),
                Offset(w * 0.78f, h * 0.18f),
                Offset(w * 0.88f, h * 0.34f)
            )
            starOffsets.forEachIndexed { sIdx, sPt ->
                val sAlpha = (shimmerAlpha + sIdx * 0.15f) % 1f
                drawCircle(
                    color = Color.White.copy(alpha = sAlpha),
                    radius = 1.8f,
                    center = sPt
                )
            }

            // D. 3D Spherical Moon Body with Pearlescent Gradient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF), // Moon specular highlight
                        Color(0xFFF1F5F9), // Pearl white
                        Color(0xFFCBD5E1), // Lunar grey
                        Color(0xFF64748B)  // Shadow rim
                    ),
                    center = Offset(celestialCenter.x - celestialRadius * 0.22f, celestialCenter.y - celestialRadius * 0.22f),
                    radius = celestialRadius * 1.1f
                ),
                radius = celestialRadius,
                center = celestialCenter
            )

            // Subtle Lunar Crater details
            drawCircle(
                color = Color(0xFF94A3B8).copy(alpha = 0.25f),
                radius = celestialRadius * 0.22f,
                center = Offset(celestialCenter.x + celestialRadius * 0.25f, celestialCenter.y - celestialRadius * 0.15f)
            )
            drawCircle(
                color = Color(0xFF94A3B8).copy(alpha = 0.20f),
                radius = celestialRadius * 0.16f,
                center = Offset(celestialCenter.x - celestialRadius * 0.15f, celestialCenter.y + celestialRadius * 0.30f)
            )
        }

        // ==========================================
        // 3. THUNDERSTORM / RAIN ATMOSPHERIC AURA
        // ==========================================
        if (isThunderstorm) {
            // Electric Violet Storm Pulse Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFB388FF).copy(alpha = 0.28f * shimmerAlpha),
                        Color(0xFF7C4DFF).copy(alpha = 0.12f * shimmerAlpha),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.52f, h * 0.64f),
                    radius = w * 0.45f
                ),
                radius = w * 0.45f,
                center = Offset(w * 0.52f, h * 0.64f)
            )
        }

        // ==========================================
        // 4. SOFT FLUFFY 3D CLOUD (Overlapping lower right)
        // ==========================================
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

        // ==========================================
        // 5. ANIMATED PRECIPITATION RAINDROPS
        // ==========================================
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
