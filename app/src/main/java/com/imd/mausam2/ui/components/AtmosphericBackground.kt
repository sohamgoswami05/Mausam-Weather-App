package com.imd.mausam2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

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
        content()
    }
}
