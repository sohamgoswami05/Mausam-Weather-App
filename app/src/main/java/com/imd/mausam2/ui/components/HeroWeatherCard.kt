package com.imd.mausam2.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imd.mausam2.data.model.CompleteWeatherData
import com.imd.mausam2.ui.theme.NeonOrange
import com.imd.mausam2.ui.theme.TextMuted
import com.imd.mausam2.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.*

/**
 * HeroWeatherCard:
 * Matches the reference weather aesthetic with bold city typography,
 * top-right audio speaker button, huge temperature display, condition & high/low,
 * 3D radiant sun and rain cloud artwork, metric pill chips (AQI, Feels, Wind),
 * and live timestamp.
 */
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
    val maxTemp = data.sevenDayForecast.firstOrNull()?.maxTemp?.toInt() ?: (data.currentTemp + 2).toInt()
    val minTemp = data.sevenDayForecast.firstOrNull()?.minTemp?.toInt() ?: (data.currentTemp - 4).toInt()
    val condition = if (isHindi) data.conditionTextHi else data.conditionText

    val currentTime = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    // Audio Speaker Breathing Animation when playing
    val infiniteTransition = rememberInfiniteTransition(label = "audioSpeaker")
    val speakerScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speakerPulse"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        // 1. Top Header Row: City Name & Audio Speaker Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Location Name (Tappable to switch city)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onLocationClick() }
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = data.location.cityName,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                )
            }

            // Top-Right Audio Speaker Button (Neural Voice Sahayak)
            IconButton(
                onClick = onAudioToggle,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPlayingAudio) NeonOrange.copy(alpha = 0.28f)
                        else Color.White.copy(alpha = 0.12f)
                    )
                    .scale(if (isPlayingAudio) speakerScale else 1f)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = if (isHindi) "सुनें (मौसम सहायक)" else "Listen Weather Brief",
                    tint = if (isPlayingAudio) NeonOrange else Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // 2. Middle Row: Big Temperature & Condition on Left, 3D Art on Right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Column: Temperature, Condition, High/Low
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${data.currentTemp.toInt()}°",
                    fontSize = 76.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    lineHeight = 78.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "$condition • H $maxTemp° L $minTemp°",
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Right: 3D Sun & Rain Cloud Artwork
            WeatherHeroArt(
                weatherCode = data.weatherCode,
                modifier = Modifier.size(135.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Quick Metric Pill Badges Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // AQI Pill Chip (Green / Warning based on CPCB scale)
            val aqiBgColor = when {
                data.aqi > 200 -> Color(0xFF451A1A)
                data.aqi > 100 -> Color(0xFF422B14)
                else -> Color(0xFF133827)
            }
            val aqiTextColor = when {
                data.aqi > 200 -> Color(0xFFF87171)
                data.aqi > 100 -> Color(0xFFFBBF24)
                else -> Color(0xFF4ADE80)
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = aqiBgColor
            ) {
                Text(
                    text = "AQI ${data.aqi}",
                    color = aqiTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            // Feels Like Pill Chip
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.12f)
            ) {
                Text(
                    text = if (isHindi) "महसूस ${data.feelsLike.toInt()}°" else "Feels ${data.feelsLike.toInt()}°",
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            // Wind Speed Pill Chip
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.12f)
            ) {
                Text(
                    text = if (isHindi) "हवा ${data.windSpeed.toInt()} km/h" else "Wind ${data.windSpeed.toInt()} km/h",
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 4. Live Observation Timestamp
        Text(
            text = if (isHindi) "समय $currentTime के अनुसार" else "as of $currentTime",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
