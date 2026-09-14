package com.imd.mausam2.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imd.mausam2.ui.theme.SkyCyan
import com.imd.mausam2.ui.theme.SunGold

@Composable
fun CoreFeaturesQuickHub(
    isHindi: Boolean,
    onRadarClick: () -> Unit,
    onAlertsClick: () -> Unit,
    onAssistantClick: () -> Unit,
    onWizardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Radar
            QuickFeaturePill(
                icon = Icons.Default.Radar,
                iconTint = SkyCyan,
                title = if (isHindi) "डॉपलर रडार" else "Radar GIS",
                subtitle = if (isHindi) "लाइव इसरो" else "Live IMD",
                accentColor = SkyCyan,
                modifier = Modifier.weight(1f),
                onClick = onRadarClick
            )

            // 2. Alerts
            QuickFeaturePill(
                icon = Icons.Default.NotificationsActive,
                iconTint = Color(0xFFF87171),
                title = if (isHindi) "मौसम अलर्ट" else "IMD Alerts",
                subtitle = if (isHindi) "4-स्तरीय" else "Official",
                accentColor = Color(0xFFF87171),
                modifier = Modifier.weight(1f),
                onClick = onAlertsClick
            )

            // 3. AI Assistant
            QuickFeaturePill(
                icon = Icons.Default.SmartToy,
                iconTint = Color(0xFFA855F7),
                title = if (isHindi) "7-दिन AI" else "7-Day AI",
                subtitle = if (isHindi) "चैटबॉट" else "Assistant",
                accentColor = Color(0xFFA855F7),
                modifier = Modifier.weight(1f),
                onClick = onAssistantClick
            )

            // 4. Personas Wizard
            QuickFeaturePill(
                icon = Icons.Default.DashboardCustomize,
                iconTint = SunGold,
                title = if (isHindi) "कार्ड सेटअप" else "Personas",
                subtitle = if (isHindi) "विज़ार्ड" else "Wizard",
                accentColor = SunGold,
                modifier = Modifier.weight(1f),
                onClick = onWizardClick
            )
        }
    }
}

@Composable
private fun QuickFeaturePill(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x330F172A),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
        shadowElevation = 6.dp,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(accentColor.copy(alpha = 0.14f), Color(0x220F172A))
                    )
                )
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}
