package com.imd.mausam2.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import java.util.Calendar

/**
 * AtmosphericBackground:
 * Renders the deep atmospheric slate-twilight background, subtle drifting clouds in the
 * upper sky, the iconic architectural landmark silhouette (Victoria Memorial / city domes)
 * at the horizon line, and radiant ethereal celestial glow around the sun, moon, rain clouds,
 * and thunderstorms.
 */
@Composable
fun AtmosphericBackground(
    weatherCode: Int,
    wallpaperTheme: com.imd.mausam2.domain.WallpaperTheme = com.imd.mausam2.domain.WallpaperTheme.SLATE,
    weatherEffectsEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val isDay = currentHour in 6..18

    val isRainy = weatherCode in listOf(51, 53, 55, 61, 63, 65, 80, 81, 82)
    val isThunderstorm = weatherCode in listOf(95, 96, 99)

    // Breathing Celestial Halo Animation
    val infiniteTransition = rememberInfiniteTransition(label = "celestialAtmosphere")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    // Thunderstorm Lightning Flash Animation
    val lightningFlash by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lightningFlash"
    )

    val gradientColors = when {
        wallpaperTheme != com.imd.mausam2.domain.WallpaperTheme.SLATE -> {
            if (isDay) wallpaperTheme.dayColors else wallpaperTheme.nightColors
        }
        isThunderstorm -> listOf(
            Color(0xFF1E172F),
            Color(0xFF261D3B),
            Color(0xFF1C182B),
            Color(0xFF0E0B16)
        )
        isRainy -> listOf(
            Color(0xFF0E1A29),
            Color(0xFF15263B),
            Color(0xFF1C2E46),
            Color(0xFF0A131F)
        )
        !isDay -> listOf(
            Color(0xFF0B111D),
            Color(0xFF121B2D),
            Color(0xFF182236),
            Color(0xFF080C14)
        )
        weatherCode in 0..1 -> listOf(
            Color(0xFF182232),
            Color(0xFF1E2B3E),
            Color(0xFF202C40),
            Color(0xFF0C1320)
        )
        weatherCode in 2..3 -> listOf(
            Color(0xFF162132),
            Color(0xFF1C2B40),
            Color(0xFF213148),
            Color(0xFF0E1624)
        )
        else -> listOf(
            Color(0xFF141E2D),
            Color(0xFF1A263A),
            Color(0xFF1E2B3E),
            Color(0xFF0F141B)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        if (weatherEffectsEnabled) {
            // Atmospheric Sky Wisps, Celestial Glow, and Landmark Silhouette Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // 1. Celestial Ambient Atmospheric Glow (Sun, Moon, Rain Clouds, Thunderstorm)
                drawCelestialAtmosphericGlow(
                    scope = this,
                    w = w,
                    h = h,
                    isDay = isDay,
                    isRainy = isRainy,
                    isThunderstorm = isThunderstorm,
                    glowPulse = glowPulse,
                    lightningFlash = lightningFlash
                )

                // 2. Soft Drifting Atmospheric Clouds in Upper-Left Sky
                drawAtmosphericCloudWisps(this, w, h)

                // 3. Horizon Landmark Skyline Silhouette (Victoria Memorial / Heritage Domes)
                drawHorizonLandmarkSilhouette(this, w, h)
            }
        }

        content()
    }
}

/**
 * Renders the prominent ethereal atmospheric glow radiating from the celestial body
 * in the upper-right sky across the hero header area.
 */
private fun drawCelestialAtmosphericGlow(
    scope: DrawScope,
    w: Float,
    h: Float,
    isDay: Boolean,
    isRainy: Boolean,
    isThunderstorm: Boolean,
    glowPulse: Float,
    lightningFlash: Float
) {
    with(scope) {
        // Celestial center aligned with WeatherHeroArt in the upper-right
        val celestialCenter = Offset(w * 0.74f, h * 0.125f)

        when {
            // A. THUNDERSTORM: Pulsing Electric Violet / Deep Magenta Storm Glow
            isThunderstorm -> {
                // Wide ambient storm aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFA855F7).copy(alpha = 0.42f * glowPulse),
                            Color(0xFF7C3AED).copy(alpha = 0.24f * glowPulse),
                            Color(0xFF6D28D9).copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        center = celestialCenter,
                        radius = w * 0.85f
                    ),
                    radius = w * 0.85f,
                    center = celestialCenter
                )

                // Intense lightning sheet flash corona
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF).copy(alpha = 0.40f * lightningFlash),
                            Color(0xFFE9D5FF).copy(alpha = 0.60f * lightningFlash),
                            Color(0xFFC084FC).copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        center = celestialCenter,
                        radius = w * 0.45f
                    ),
                    radius = w * 0.45f,
                    center = celestialCenter
                )
            }

            // B. RAINY: Luminous Cyan Rain-Mist Aura
            isRainy -> {
                // Wide ambient cyan rain mist glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF38BDF8).copy(alpha = 0.38f * glowPulse),
                            Color(0xFF0284C7).copy(alpha = 0.22f * glowPulse),
                            Color(0xFF0369A1).copy(alpha = 0.09f),
                            Color.Transparent
                        ),
                        center = celestialCenter,
                        radius = w * 0.82f
                    ),
                    radius = w * 0.82f,
                    center = celestialCenter
                )

                // Concentrated dewy mist cloud corona
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFBAE6FD).copy(alpha = 0.52f * glowPulse),
                            Color(0xFF38BDF8).copy(alpha = 0.26f),
                            Color(0xFF0284C7).copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        center = celestialCenter,
                        radius = w * 0.44f
                    ),
                    radius = w * 0.44f,
                    center = celestialCenter
                )
            }

            // C. NIGHTTIME: Luminous Silver Lunar Halo
            !isDay -> {
                // Wide ambient lunar moonlight aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF93C5FD).copy(alpha = 0.34f * glowPulse),
                            Color(0xFF60A5FA).copy(alpha = 0.18f * glowPulse),
                            Color(0xFF3B82F6).copy(alpha = 0.07f),
                            Color.Transparent
                        ),
                        center = celestialCenter,
                        radius = w * 0.80f
                    ),
                    radius = w * 0.80f,
                    center = celestialCenter
                )

                // Concentrated pearlescent silver moon corona
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF).copy(alpha = 0.55f * glowPulse),
                            Color(0xFFE2E8F0).copy(alpha = 0.32f),
                            Color(0xFF93C5FD).copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = celestialCenter,
                        radius = w * 0.42f
                    ),
                    radius = w * 0.42f,
                    center = celestialCenter
                )
            }

            // D. DAYTIME: Radiant Golden Solar Halo (Matching reference app)
            else -> {
                // Wide ambient golden solar warmth radiating across the sky
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFCA28).copy(alpha = 0.42f * glowPulse),
                            Color(0xFFF59E0B).copy(alpha = 0.25f * glowPulse),
                            Color(0xFFD97706).copy(alpha = 0.11f),
                            Color.Transparent
                        ),
                        center = celestialCenter,
                        radius = w * 0.85f
                    ),
                    radius = w * 0.85f,
                    center = celestialCenter
                )

                // Concentrated solar corona halo
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFFBEB).copy(alpha = 0.65f * glowPulse),
                            Color(0xFFFEF08A).copy(alpha = 0.40f * glowPulse),
                            Color(0xFFFBBF24).copy(alpha = 0.18f),
                            Color.Transparent
                        ),
                        center = celestialCenter,
                        radius = w * 0.46f
                    ),
                    radius = w * 0.46f,
                    center = celestialCenter
                )
            }
        }
    }
}

private fun drawAtmosphericCloudWisps(scope: DrawScope, w: Float, h: Float) {
    with(scope) {
        val cloudBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF94A3B8).copy(alpha = 0.16f), Color.Transparent),
            startY = 0f,
            endY = h * 0.18f
        )

        val p = Path().apply {
            moveTo(0f, h * 0.04f)
            cubicTo(w * 0.15f, h * 0.02f, w * 0.25f, h * 0.08f, w * 0.40f, h * 0.05f)
            cubicTo(w * 0.50f, h * 0.03f, w * 0.60f, h * 0.09f, w * 0.70f, h * 0.04f)
            lineTo(w * 0.70f, 0f)
            lineTo(0f, 0f)
            close()
        }
        drawPath(p, brush = cloudBrush)
    }
}

private fun drawHorizonLandmarkSilhouette(scope: DrawScope, w: Float, h: Float) {
    with(scope) {
        val horizonY = h * 0.44f // Positioned right behind the top cards
        val silhouetteColor = Color(0x38070C16)

        val monumentPath = Path().apply {
            moveTo(0f, horizonY + 30f)
            lineTo(w * 0.18f, horizonY + 28f)

            // Distant trees & architectural pavilions
            lineTo(w * 0.24f, horizonY + 20f)
            lineTo(w * 0.28f, horizonY + 26f)

            // Left Colonnaded Pavilion & Chattri
            lineTo(w * 0.34f, horizonY + 14f)
            lineTo(w * 0.36f, horizonY + 14f)
            lineTo(w * 0.38f, horizonY + 24f)

            // Central Grand Victoria Memorial Dome
            lineTo(w * 0.44f, horizonY + 16f)
            // Left shoulder
            lineTo(w * 0.46f, horizonY + 10f)
            // Main Neoclassical Dome Arc
            addArc(
                oval = Rect(left = w * 0.46f, top = horizonY - 26f, right = w * 0.54f, bottom = horizonY + 14f),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 180f
            )
            // Finial / Angel Spire
            moveTo(w * 0.50f, horizonY - 26f)
            lineTo(w * 0.50f, horizonY - 34f)
            lineTo(w * 0.50f, horizonY - 26f)

            // Right shoulder
            moveTo(w * 0.54f, horizonY + 10f)
            lineTo(w * 0.56f, horizonY + 16f)

            // Right Colonnaded Pavilion & Chattri
            lineTo(w * 0.62f, horizonY + 24f)
            lineTo(w * 0.64f, horizonY + 14f)
            lineTo(w * 0.66f, horizonY + 14f)
            lineTo(w * 0.72f, horizonY + 26f)

            // Rolling horizon line to edge
            lineTo(w * 0.82f, horizonY + 22f)
            lineTo(w, horizonY + 28f)
            lineTo(w, horizonY + 60f)
            lineTo(0f, horizonY + 60f)
            close()
        }

        // Render Landmark Silhouette with Misty Vertical Gradient
        drawPath(
            path = monumentPath,
            brush = Brush.verticalGradient(
                colors = listOf(silhouetteColor, Color.Transparent),
                startY = horizonY - 35f,
                endY = horizonY + 60f
            )
        )
    }
}
