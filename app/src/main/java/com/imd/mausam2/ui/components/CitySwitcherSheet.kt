package com.imd.mausam2.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imd.mausam2.data.model.LocationInfo
import com.imd.mausam2.data.repository.WeatherRepository
import com.imd.mausam2.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySwitcherSheet(
    currentCity: String,
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onCitySelected: (LocationInfo) -> Unit,
    onRequestGps: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DeepBlue,
        scrimColor = Color.Black.copy(alpha = 0.65f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = if (isHindi) "स्थान चुनें (Select Location)" else "Select Location",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = if (isHindi) "विभिन्न भारतीय मौसम परिदृश्यों का लाइव परीक्षण करें" else "Switch Indian cities to test various persona scenarios live",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
            )

            // Current GPS Location Button
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SkyCyan.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, SkyCyan.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        onRequestGps()
                        onDismiss()
                    }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = "GPS",
                        tint = SkyCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isHindi) "वर्तमान जीपीएस स्थान का उपयोग करें" else "Use Current Device GPS Location",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isHindi) "रीयल-टाइम अक्षांश/देशांतर डिटेक्शन" else "Real-time satellite coordinates & forecast",
                            color = SkyCyan,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isHindi) "प्रमुख भारतीय शहर (डेमो परिदृश्य)" else "Indian Scenario Cities (Hackathon Presets)",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                items(WeatherRepository.PRESET_CITIES) { city ->
                    val isSelected = city.cityName == currentCity
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) SkyCyan else Color.White.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onCitySelected(city)
                                onDismiss()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationCity,
                                    contentDescription = city.cityName,
                                    tint = if (isSelected) SkyCyan else Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = city.cityName,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = city.stateName + " • " + getCityHighlight(city.cityName),
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (isSelected) {
                                Text(
                                    text = "Active",
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
}

private fun getCityHighlight(city: String): String = when (city) {
    "New Delhi" -> "AQI & Smog Focus"
    "Goa (Panaji)" -> "Tides & Waves Focus"
    "Mumbai" -> "Coastal & Rain Focus"
    "Bengaluru" -> "Fitness & Run Focus"
    "Ludhiana" -> "Agri & Soil Moisture Focus"
    "Shimla" -> "Mountain & Frost Focus"
    "Chennai" -> "Maritime & Heat Focus"
    else -> "General Weather"
}
