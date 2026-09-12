package com.imd.mausam2.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imd.mausam2.data.model.CompleteWeatherData
import com.imd.mausam2.data.model.IMDWarningLevel
import com.imd.mausam2.ui.theme.*

@Composable
fun HeroWeatherCard(
    data: CompleteWeatherData,
    isHindi: Boolean,
    smartGlanceText: String,
    isPlayingAudio: Boolean,
    onAudioToggle: () -> Unit,
    onLocationClick: () -> Unit,
    onCustomizeClick: () -> Unit,
    onLanguageToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // TOP APP BAR / LOCATION & ACTIONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Location Chip
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onLocationClick() },
                color = Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = SkyCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${data.location.cityName} ▾",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Right Actions: Offline/Cache badge, Language, Settings
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Offline / Live Indicator
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (data.isFromCache) Color(0x33FFA000) else Color(0x334CAF50)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (data.isFromCache) Color(0xFFFFB300) else Color(0xFF4CAF50))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (data.isFromCache) (if (isHindi) "ऑफ़लाइन 7-दिन" else "7-Day Cached") else (if (isHindi) "लाइव सिंक" else "Live"),
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Language Toggle
                IconButton(
                    onClick = onLanguageToggle,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                ) {
                    Text(
                        text = if (isHindi) "EN" else "हि",
                        color = SunGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Customize Cards
                IconButton(
                    onClick = onCustomizeClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Customize Cards",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // MAIN WEATHER HERO GLASS CARD
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundAlpha = 0.28f,
            borderAlpha = 0.25f
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Temperature and Weather Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${data.currentTemp.toInt()}°",
                            fontSize = 68.sp,
                            fontWeight = FontWeight.Light,
                            color = Color.White,
                            lineHeight = 70.sp
                        )
                        Text(
                            text = if (isHindi) data.conditionTextHi else data.conditionText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = SkyCyan
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isHindi) "महसूस: ${data.feelsLike.toInt()}°C" else "Feels like ${data.feelsLike.toInt()}°C",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }

                    // Weather Emblem
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(SkyCyan.copy(alpha = 0.25f), Color.Transparent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = when {
                            data.weatherCode == 0 -> Icons.Default.WbSunny
                            data.weatherCode in listOf(1, 2, 3) -> Icons.Default.CloudQueue
                            data.weatherCode in listOf(45, 48) -> Icons.Default.Deblur
                            data.weatherCode in listOf(95, 96, 99) -> Icons.Default.FlashOn
                            else -> Icons.Default.WaterDrop
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = "Weather Icon",
                            tint = if (data.weatherCode == 0) SunGold else SkyCyan,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.White.copy(alpha = 0.12f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(14.dp))

                // Quick Metrics Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricItem(
                        icon = Icons.Default.Air,
                        label = if (isHindi) "हवा" else "Wind",
                        value = "${data.windSpeed.toInt()} km/h"
                    )
                    MetricItem(
                        icon = Icons.Default.WaterDrop,
                        label = if (isHindi) "नमी" else "Humidity",
                        value = "${data.humidity}%"
                    )
                    MetricItem(
                        icon = Icons.Default.WbSunny,
                        label = if (isHindi) "यूवी" else "UV Index",
                        value = "${data.uvIndex.toInt()}"
                    )
                    MetricItem(
                        icon = Icons.Default.Favorite,
                        label = "AQI",
                        value = "${data.aqi}",
                        valueColor = if (data.aqi > 200) WarningRed else SkyCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // OFFICIAL IMD WARNING BANNER
        val alertColor = Color(data.imdAlert.level.colorHex)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = alertColor.copy(alpha = 0.18f),
            border = androidx.compose.foundation.BorderStroke(1.dp, alertColor.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(alertColor.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (data.imdAlert.level == IMDWarningLevel.GREEN) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = "IMD Alert",
                        tint = alertColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHindi) data.imdAlert.headlineHi else data.imdAlert.headline,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isHindi) data.imdAlert.adviceHi else data.imdAlert.advice,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // SMART GLANCE AI & MAUSAM AUDIO SAHAYAK
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundAlpha = 0.25f,
            cornerRadius = 18.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // AI Sparkle Badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SkyCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Smart Glance",
                        tint = SkyCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "मौसम सहायक (AI Glance)" else "Smart Glance (AI Insight)",
                            color = SkyCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        // TTS Audio Sahayak Button
                        val infiniteTransition = rememberInfiniteTransition(label = "audioWave")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = if (isPlayingAudio) 1.25f else 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(500, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "scale"
                        )

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onAudioToggle() }
                                .scale(scale),
                            color = if (isPlayingAudio) NeonOrange.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isPlayingAudio) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                    contentDescription = "Audio Bulletin",
                                    tint = if (isPlayingAudio) NeonOrange else Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isPlayingAudio) (if (isHindi) "सुन रहे हैं..." else "Playing...") else (if (isHindi) "सुनें" else "Listen"),
                                    color = if (isPlayingAudio) NeonOrange else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = smartGlanceText,
                        color = Color.White.copy(alpha = 0.95f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color = Color.White
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = SkyCyan,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = TextMuted, fontSize = 11.sp)
    }
}
