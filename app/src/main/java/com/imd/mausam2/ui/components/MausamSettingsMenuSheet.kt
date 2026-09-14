package com.imd.mausam2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imd.mausam2.ui.theme.SkyCyan
import com.imd.mausam2.ui.theme.SunGold
import com.imd.mausam2.ui.theme.TextMuted
import com.imd.mausam2.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MausamSettingsMenuSheet(
    currentTab: NavTab,
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onTabSelected: (NavTab) -> Unit,
    onLanguageToggle: () -> Unit,
    onOpenOnboarding: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xF50F172A),
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 22.dp, vertical = 8.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isHindi) "मौसम नेविगेशन एवं सेटिंग्स" else "Mausam 2.0 Navigation",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isHindi) "त्वरित दृश्य एवं प्राथमिकताएं" else "Quick Views & Preferences",
                        color = SkyCyan,
                        fontSize = 12.sp
                    )
                }

                // Language Toggle Pill
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier.clickable { onLanguageToggle() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Language",
                            tint = SunGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "English" else "हिन्दी",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Navigation Items Grid
            Text(
                text = if (isHindi) "अनुभाग स्विच करें" else "SWITCH SECTION",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NavMenuItem(
                    title = if (isHindi) "होम डैशबोर्ड" else "Home Dashboard",
                    subtitle = if (isHindi) "मौसम अवलोकन एवं मुख्य कार्ड" else "Weather overview & insight cards",
                    icon = Icons.Default.Home,
                    isSelected = currentTab == NavTab.HOME,
                    onClick = {
                        onTabSelected(NavTab.HOME)
                        onDismiss()
                    }
                )

                NavMenuItem(
                    title = if (isHindi) "आईएमडी डॉपलर एवं इसरो रडार" else "ISRO MOSDAC & IMD Radar",
                    subtitle = if (isHindi) "लाइव डॉपलर अबकास्ट, इन्सेट-3DR एवं दामिनी ग्रिड" else "Live DWR nowcast, INSAT-3DR & Damini grid",
                    icon = Icons.Default.Radar,
                    isSelected = currentTab == NavTab.RADAR,
                    onClick = {
                        onTabSelected(NavTab.RADAR)
                        onDismiss()
                    }
                )

                NavMenuItem(
                    title = if (isHindi) "मौसम चेतावनियां (आईएमडी अलर्ट)" else "IMD 4-Stage Warning Alerts",
                    subtitle = if (isHindi) "लाल, नारंगी एवं पीली चेतावनी विवरण" else "Red, orange & yellow regional warnings",
                    icon = Icons.Default.Notifications,
                    isSelected = currentTab == NavTab.ALERTS,
                    onClick = {
                        onTabSelected(NavTab.ALERTS)
                        onDismiss()
                    }
                )

                NavMenuItem(
                    title = if (isHindi) "प्रोफाइल एवं 7-दिवसीय AI चैटबॉट" else "Profiles & 7-Day AI Chatbot",
                    subtitle = if (isHindi) "उपभोक्ता प्रोफाइल एवं स्मार्ट चैट सहायक" else "User personas & conversational assistant",
                    icon = Icons.Default.Person,
                    isSelected = currentTab == NavTab.PERSONAS,
                    onClick = {
                        onTabSelected(NavTab.PERSONAS)
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Re-run Onboarding Persona Wizard
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onOpenOnboarding()
                    }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SkyCyan.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = SkyCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (isHindi) "व्यक्तिगत प्राथमिकताएं बदलें" else "Customize Consumer Personas",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isHindi) "ऑनबोर्डिंग विज़ार्ड पुनः प्रारंभ करें" else "Re-run initial onboarding selection",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NavMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) SkyCyan.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, SkyCyan.copy(alpha = 0.5f)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) SkyCyan else Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isSelected) Color(0xFF0F172A) else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isSelected) SkyCyan else Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(SkyCyan)
                )
            }
        }
    }
}
