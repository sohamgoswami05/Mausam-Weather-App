package com.imd.mausam2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imd.mausam2.data.model.CompleteWeatherData
import com.imd.mausam2.data.model.IMDWarningLevel
import com.imd.mausam2.ui.components.GlassCard
import com.imd.mausam2.ui.theme.*

@Composable
fun AlertsScreen(
    data: CompleteWeatherData,
    isHindi: Boolean,
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Header with Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Main",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = if (isHindi) "आईएमडी चेतावनी बुलेटिन" else "Official IMD Warning Bulletins",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isHindi) "भारत मौसम विज्ञान विभाग (MoES) द्वारा जारी" else "Issued by India Meteorological Department (MoES)",
                    color = SkyCyan,
                    fontSize = 11.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // 1. Current City Active Alert
            item {
                val alertColor = Color(data.imdAlert.level.colorHex)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = alertColor.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, alertColor.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(alertColor.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (data.imdAlert.level == IMDWarningLevel.GREEN) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = alertColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isHindi) data.imdAlert.level.labelHi else data.imdAlert.level.labelEn,
                                    color = alertColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${data.location.cityName}, ${data.location.stateName}",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isHindi) data.imdAlert.descriptionHi else data.imdAlert.description,
                            color = Color.White,
                            fontSize = 13.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "👉 " + if (isHindi) data.imdAlert.adviceHi else data.imdAlert.advice,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }

            // 2. Official 4-Stage Warning Guide
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = if (isHindi) "आईएमडी 4-स्तरीय रंग चेतावनी प्रणाली" else "IMD 4-Color Warning Schema",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        AlertSchemaRow(color = Color(0xFF4CAF50), label = "GREEN", meaning = "No Warning. Safe weather conditions.")
                        AlertSchemaRow(color = Color(0xFFFFC107), label = "YELLOW", meaning = "Watch. Be updated on localized changes.")
                        AlertSchemaRow(color = Color(0xFFFF9800), label = "ORANGE", meaning = "Alert. Be prepared for severe disruptions.")
                        AlertSchemaRow(color = Color(0xFFE53935), label = "RED", meaning = "Warning. Take immediate action to protect life/property.")
                    }
                }
            }

            // 3. National Disaster Helplines
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.PhoneInTalk, contentDescription = null, tint = SunGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "राष्ट्रीय आपातकालीन सहायता नंबर" else "National Emergency Helplines",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "• National Emergency: 112", color = TextSecondary, fontSize = 12.sp)
                        Text(text = "• Disaster Management (NDMA): 1070", color = TextSecondary, fontSize = 12.sp)
                        Text(text = "• District Relief Center: 1077", color = TextSecondary, fontSize = 12.sp)
                        Text(text = "• Indian Coast Guard (Maritime SAR): 1554", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertSchemaRow(color: Color, label: String, meaning: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = RoundedCornerShape(6.dp), color = color) {
            Text(text = label, color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = meaning, color = TextSecondary, fontSize = 11.sp)
    }
}
