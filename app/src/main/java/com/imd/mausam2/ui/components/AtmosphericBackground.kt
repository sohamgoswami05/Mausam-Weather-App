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
            Color(0xFF0F3460),
            Color(0xFF16213E),
            Color(0xFF1A1A2E)
        )
        2, 3 -> listOf(
            Color(0xFF1E3C72),
            Color(0xFF2A5298),
            Color(0xFF111927)
        )
        45, 48 -> listOf(
            Color(0xFF2C3E50),
            Color(0xFF1F2833),
            Color(0xFF0B0C10)
        )
        51, 53, 55, 61, 63, 65, 80, 81, 82 -> listOf(
            Color(0xFF0D324D),
            Color(0xFF1B2A47),
            Color(0xFF0A1118)
        )
        95, 96, 99 -> listOf(
            Color(0xFF2E1C4E),
            Color(0xFF1D1B36),
            Color(0xFF0B0914)
        )
        else -> listOf(
            Color(0xFF0A192F),
            Color(0xFF112240),
            Color(0xFF020C1B)
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
