package com.imd.mausam2.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imd.mausam2.ui.theme.DeepBlue
import com.imd.mausam2.ui.theme.SkyCyan
import com.imd.mausam2.ui.theme.TextMuted

enum class NavTab(val titleEn: String, val titleHi: String) {
    HOME("Home", "होम"),
    RADAR("Radar", "रडार"),
    ALERTS("Alerts", "अलर्ट"),
    PERSONAS("Profiles", "प्रोफाइल")
}

@Composable
fun BottomNavBar(
    currentTab: NavTab,
    isHindi: Boolean,
    onTabSelected: (NavTab) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xDD0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTab.values().forEach { tab ->
                val isSelected = currentTab == tab
                val icon = when (tab) {
                    NavTab.HOME -> Icons.Default.Home
                    NavTab.RADAR -> Icons.Default.Radar
                    NavTab.ALERTS -> Icons.Default.Notifications
                    NavTab.PERSONAS -> Icons.Default.Person
                }

                val tintColor by animateColorAsState(
                    if (isSelected) SkyCyan else TextMuted,
                    label = "tabTint"
                )

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onTabSelected(tab) },
                    color = if (isSelected) SkyCyan.copy(alpha = 0.18f) else Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.titleEn,
                            tint = tintColor,
                            modifier = Modifier.size(20.dp)
                        )

                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) tab.titleHi else tab.titleEn,
                                color = SkyCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
