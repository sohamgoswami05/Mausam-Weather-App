package com.imd.mausam2.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * AtmosphericBackground:
 * Renders the deep atmospheric slate-twilight background, subtle drifting clouds in the
 * upper sky, and the iconic architectural landmark silhouette (Victoria Memorial / city domes)
 * at the horizon line behind the insight cards.
 */
@Composable
fun AtmosphericBackground(
    weatherCode: Int,
    content: @Composable () -> Unit
) {
    val gradientColors = when (weatherCode) {
        0, 1 -> listOf(
            Color(0xFF101B2B),
            Color(0xFF162338),
            Color(0xFF1B2A42),
            Color(0xFF0C1320)
        )
        2, 3 -> listOf(
            Color(0xFF131F33),
            Color(0xFF1A2A44),
            Color(0xFF20324E),
            Color(0xFF0E1624)
        )
        45, 48 -> listOf(
            Color(0xFF1A222D),
            Color(0xFF232D3B),
            Color(0xFF1A232E),
            Color(0xFF0F141B)
        )
        51, 53, 55, 61, 63, 65, 80, 81, 82 -> listOf(
            Color(0xFF0E1A29),
            Color(0xFF15263B),
            Color(0xFF1C2E46),
            Color(0xFF0A131F)
        )
        95, 96, 99 -> listOf(
            Color(0xFF1E172F),
            Color(0xFF261D3B),
            Color(0xFF1C182B),
            Color(0xFF0E0B16)
        )
        else -> listOf(
            Color(0xFF0E1726),
            Color(0xFF152238),
            Color(0xFF1A2740),
            Color(0xFF0A111C)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        // Atmospheric Sky Wisps & Landmark Silhouette Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Soft Drifting Atmospheric Clouds in Upper-Left Sky
            drawAtmosphericCloudWisps(this, w, h)

            // 2. Horizon Landmark Skyline Silhouette (Victoria Memorial / Heritage Domes)
            drawHorizonLandmarkSilhouette(this, w, h)
        }

        content()
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
