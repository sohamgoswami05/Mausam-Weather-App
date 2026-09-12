package com.imd.mausam2.ui.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
fun CommuterCard(data: CompleteWeatherData, isHindi: Boolean) {
    val visColor = when {
        data.visibilityMeters < 200 -> Color(0xFFE53935)
        data.visibilityMeters < 500 -> Color(0xFFFFB300)
        else -> Color(0xFF4CAF50)
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
                            .background(Color(0xFF29B6F6).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Commute",
                            tint = Color(0xFF29B6F6),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "दैनिक यात्रा एवं स्कूल (Commute & School)" else "Daily Commuters & School",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = visColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (data.visibilityMeters < 300) (if (isHindi) "घना कोहरा" else "Dense Fog") else (if (isHindi) "साफ़ रास्ता" else "Clear Transit"),
                        color = visColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // School & Morning Transit Window
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "School",
                    tint = SunGold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isHindi) "स्कूल कम्यूट विंडो (07:00 AM - 09:00 AM)" else "School Commute Window (07:00 - 09:00 AM)",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = if (isHindi) "स्थिति: " + (if (data.visibilityMeters < 300) "धीमी गति से वाहन चलाएं (कोहरा)" else "सुगम एवं सामान्य") else "Status: " + data.schoolCommuteRating,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visibility & Waterlogging
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Visibility",
                        tint = SkyCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = if (isHindi) "सड़क दृश्यता" else "Road Visibility",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "${data.visibilityMeters} meters",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = if (isHindi) "जलभराव जोखिम" else "Waterlogging Risk",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                    Text(
                        text = if (data.weatherCode in listOf(65, 81, 82, 95)) "High on Underpasses" else "Low / Minimal",
                        color = if (data.weatherCode in listOf(65, 81, 82, 95)) WarningRed else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
