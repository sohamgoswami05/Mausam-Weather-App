package com.imd.mausam2.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * FloatingActionDock:
 * Matches the floating navigation dock seen in the modern design:
 * Left pill capsule: [ 📍 Location  ➕ Add/Customize  ⚙️ Menu/Settings ]
 * Right squircle button: [ 🔄 Refresh ] in blue-cyan gradient with rotation animation.
 */
@Composable
fun FloatingActionDock(
    onLocationClick: () -> Unit,
    onCustomizeClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onRefreshClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isManualSpinning by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "refreshSpin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinAngle"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Pill Capsule: [ 📍 ➕ ⚙️ ]
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xF0182234),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            shadowElevation = 10.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Location Pin Button
                IconButton(
                    onClick = onLocationClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Switch City",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // 2. Add / Customize Persona Cards Button
                IconButton(
                    onClick = onCustomizeClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Customize Cards",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // 3. Settings / Features Menu Button
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings & Navigation",
                        tint = Color.White,
                        modifier = Modifier.size(21.dp)
                    )
                }
            }
        }

        // Right Squircle Button: [ 🔄 Refresh ]
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF2563EB), Color(0xFF60A5FA))
                    )
                )
                .clickable {
                    isManualSpinning = true
                    onRefreshClick()
                    coroutineScope.launch {
                        delay(1200)
                        isManualSpinning = false
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            val shouldSpin = isLoading || isManualSpinning
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh Data",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(if (shouldSpin) spinAngle else 0f)
            )
        }
    }
}
