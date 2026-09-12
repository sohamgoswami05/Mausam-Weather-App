package com.imd.mausam2.ui.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Masks
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.imd.mausam2.ui.components.GlassCard
import com.imd.mausam2.ui.theme.*

@Composable
fun HealthCard(data: CompleteWeatherData, isHindi: Boolean) {
    val aqiColor = when {
        data.aqi > 300 -> Color(0xFFD32F2F) // Severe (Red)
        data.aqi > 200 -> Color(0xFFFF9800) // Very Poor (Orange)
        data.aqi > 100 -> Color(0xFFFFEB3B) // Moderate (Yellow)
        else -> Color(0xFF4CAF50) // Good (Green)
    }

    val aqiStatus = when {
        data.aqi > 300 -> if (isHindi) "गंभीर (Severe)" else "Severe Hazard"
        data.aqi > 200 -> if (isHindi) "खराब (Poor)" else "Poor / Unhealthy"
        data.aqi > 100 -> if (isHindi) "मध्यम (Moderate)" else "Moderate"
        else -> if (isHindi) "उत्कृष्ट (Good)" else "Good / Clean"
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE91E63).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Health",
                            tint = Color(0xFFFF4081),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "स्वास्थ्य एवं वायु गुणवत्ता (Health & AQI)" else "Health & Air Quality",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = aqiColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = aqiStatus,
                        color = aqiColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main AQI Meter Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "AQI ${data.aqi}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isHindi) "राष्ट्रीय वायु गुणवत्ता सूचकांक" else "National Air Quality Index",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "PM2.5: ${data.pm25.toInt()} µg/m³",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "PM10: ${data.pm10.toInt()} µg/m³",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = (data.aqi.coerceAtMost(500) / 500f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = aqiColor,
                trackColor = Color.White.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))

            // UV and Asthma Alert Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // UV Index Box
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "UV",
                        tint = SunGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "UV ${data.uvIndex.toInt()} (" + (if (data.uvIndex > 7) "Very High" else "Moderate") + ")",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (data.uvIndex > 6) "SPF 50+ Sunscreen" else "SPF 30+ recommended",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // Asthma / Allergy Advisory Box
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Masks,
                        contentDescription = "Mask",
                        tint = if (data.aqi > 200) WarningRed else SkyCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isHindi) "अस्थमा जोखिम: " + (if (data.aqi > 200) "उच्च" else "कम") else "Asthma Risk: " + (if (data.aqi > 200) "High" else "Low"),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (data.aqi > 200) "N95 Mask recommended" else "Normal outdoor activity",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
