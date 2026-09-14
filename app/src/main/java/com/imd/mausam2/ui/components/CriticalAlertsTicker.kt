package com.imd.mausam2.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imd.mausam2.data.model.CompleteWeatherData
import com.imd.mausam2.data.model.IMDWarningLevel
import com.imd.mausam2.ui.theme.*

data class TickerAlert(
    val id: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val textEn: String,
    val textHi: String,
    val color: Color
)

@Composable
fun CriticalAlertsTicker(
    data: CompleteWeatherData,
    isHindi: Boolean,
    onAlertClick: () -> Unit
) {
    // Only display top ticker during actual IMD warnings or severe hazard
    if (data.imdAlert.level == IMDWarningLevel.GREEN && data.aqi <= 200 && data.lightningProximityKm == null) {
        return
    }

    val alertList = remember(data) {
        listOf(
            TickerAlert(
                id = "imd_main",
                icon = if (data.imdAlert.level == IMDWarningLevel.GREEN) Icons.Default.Info else Icons.Default.Warning,
                textEn = "IMD Nowcast: ${data.imdAlert.headline} - ${data.imdAlert.advice}",
                textHi = "आईएमडी नाउकास्ट: ${data.imdAlert.headlineHi} - ${data.imdAlert.adviceHi}",
                color = Color(data.imdAlert.level.colorHex)
            ),
            TickerAlert(
                id = "aqi_ticker",
                icon = Icons.Default.NotificationsActive,
                textEn = "Air Quality: AQI ${data.aqi} in ${data.location.cityName}. Mask recommended outdoors.",
                textHi = "वायु गुणवत्ता: ${data.location.cityName} में AQI ${data.aqi}। मास्क अवश्य लगाएं।",
                color = if (data.aqi > 200) WarningRed else SkyCyan
            ),
            TickerAlert(
                id = "commute_ticker",
                icon = Icons.Default.Info,
                textEn = "School & Transit: Visibility ${data.visibilityMeters}m. Road transit is ${data.schoolCommuteRating}.",
                textHi = "स्कूल एवं आवागमन: दृश्यता ${data.visibilityMeters}मी। सड़क मार्ग की स्थिति सामान्य।",
                color = BrightTeal
            ),
            TickerAlert(
                id = "coastal_ticker",
                icon = Icons.Default.ElectricBolt,
                textEn = "Maritime Bulletin: Sea state '${data.seaCondition}' with ${data.waveHeight}m swells.",
                textHi = "तटीय बुलेटिन: समुद्र की स्थिति '${data.seaConditionHi}' एवं लहरें ${data.waveHeight}मी।",
                color = SunGold
            )
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onAlertClick() },
        shape = RoundedCornerShape(14.dp),
        color = Color(0x331E293B),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pulsing Live Badge
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = WarningRed.copy(alpha = 0.25f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(WarningRed.copy(alpha = alpha))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHindi) "ताज़ा अपडेट्स" else "URGENT",
                        color = WarningRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Scrollable / Horizontal Ticker Items
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(alertList) { alert ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = alert.icon,
                            contentDescription = null,
                            tint = alert.color,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isHindi) alert.textHi else alert.textEn,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "•", color = Color.White.copy(alpha = 0.3f), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
