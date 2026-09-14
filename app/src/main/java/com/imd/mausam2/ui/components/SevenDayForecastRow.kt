package com.imd.mausam2.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imd.mausam2.domain.WeatherUnitHelper
import com.imd.mausam2.data.model.DayForecast
import com.imd.mausam2.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SevenDayForecastRow(
    days: List<DayForecast>,
    isHindi: Boolean,
    isFahrenheit: Boolean = false
) {
    var selectedDay by remember { mutableStateOf<DayForecast?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isHindi) "7-दिवसीय पूर्वानुमान (7-Day Forecast)" else "7-Day Prediction Forecast",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (isHindi) "विवरण हेतु टैप करें" else "Tap for hourly",
                color = SkyCyan,
                fontSize = 11.sp
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(days) { index, day ->
                Surface(
                    modifier = Modifier
                        .width(92.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { selectedDay = day },
                    shape = RoundedCornerShape(18.dp),
                    color = if (index == 0) SkyCyan.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.08f),
                    border = BorderStroke(
                        1.dp,
                        if (index == 0) SkyCyan.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.15f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (index == 0) (if (isHindi) "आज" else "Today") else day.dayOfWeek,
                            color = if (index == 0) SkyCyan else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val icon = when {
                            day.weatherCode == 0 -> Icons.Default.WbSunny
                            day.weatherCode in listOf(1, 2, 3) -> Icons.Default.CloudQueue
                            day.weatherCode in listOf(95, 96, 99) -> Icons.Default.FlashOn
                            else -> Icons.Default.WaterDrop
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = day.conditionText,
                            tint = if (day.weatherCode == 0) SunGold else SkyCyan,
                            modifier = Modifier.size(26.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Rain probability badge
                        if (day.rainProbability > 10) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SkyCyan.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${day.rainProbability}%",
                                    color = SkyCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = WeatherUnitHelper.formatTemp(day.maxTemp, isFahrenheit),
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = WeatherUnitHelper.formatTemp(day.minTemp, isFahrenheit),
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    // Modal Sheet for Hourly Breakdown
    selectedDay?.let { day ->
        ModalBottomSheet(
            onDismissRequest = { selectedDay = null },
            containerColor = DeepBlue,
            scrimColor = Color.Black.copy(alpha = 0.6f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "${day.dayOfWeek} (${day.date}) - " + if (isHindi) day.conditionTextHi else day.conditionText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isHindi) "प्रति घंटे का विस्तृत पूर्वानुमान" else "Detailed Hourly Breakdown",
                    fontSize = 13.sp,
                    color = SkyCyan
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(day.hourlyList.size) { i ->
                        val hour = day.hourlyList[i]
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                            modifier = Modifier.width(76.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = hour.timeStr, color = TextMuted, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Icon(
                                    imageVector = if (hour.weatherCode == 0) Icons.Default.WbSunny else Icons.Default.CloudQueue,
                                    contentDescription = null,
                                    tint = if (hour.weatherCode == 0) SunGold else SkyCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = WeatherUnitHelper.formatTempWithUnit(hour.temp, isFahrenheit),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "🌧 ${hour.rainChance}%",
                                    color = SkyCyan,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
