package com.imd.mausam2.ui.cards

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imd.mausam2.data.model.CompleteWeatherData
import com.imd.mausam2.ui.components.GlassCard
import com.imd.mausam2.ui.theme.*
import kotlin.math.sin

@Composable
fun BeachCard(data: CompleteWeatherData, isHindi: Boolean) {
    val flagColor = when {
        data.waveHeight > 2.0 -> Color(0xFFD32F2F)
        data.waveHeight > 1.2 -> Color(0xFFFFB300)
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
                            .background(BrightTeal.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Waves,
                            contentDescription = "Beach",
                            tint = BrightTeal,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "समुद्र, ज्वार एवं सर्फ (Beach & Surf)" else "Beach, Tides & Maritime",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Sea Safety Flag
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = flagColor.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Safety Flag",
                            tint = flagColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isHindi) data.seaConditionHi else data.seaCondition,
                            color = flagColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Swell height & Water temp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${data.waveHeight} m",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isHindi) "लहर ऊंचाई (Wave & Swell)" else "Wave & Swell Height",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "28°C",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrightTeal
                    )
                    Text(
                        text = if (isHindi) "समुद्र जल तापमान" else "Coastal Water Temp",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visual Sinusoidal Tide Curve Graph
            Text(
                text = if (isHindi) "24-घंटे का ज्वार-भाटा चक्र (Tide Curve)" else "24-Hour Tidal Cycle Curve",
                color = TextSecondary,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.04f))
            ) {
                val w = size.width
                val h = size.height
                val path = Path()

                path.moveTo(0f, h * 0.5f)
                for (x in 0..w.toInt() step 4) {
                    val angle = (x / w) * (2 * Math.PI * 2)
                    val y = (h * 0.5f) + (sin(angle).toFloat() * (h * 0.35f))
                    path.lineTo(x.toFloat(), y)
                }

                drawPath(
                    path = path,
                    brush = Brush.horizontalGradient(listOf(BrightTeal, SkyCyan, SunGold)),
                    style = Stroke(width = 2.5f)
                )

                // High Tide marker
                drawCircle(color = SunGold, radius = 4.5f, center = Offset(w * 0.25f, h * 0.15f))
                // Low Tide marker
                drawCircle(color = BrightTeal, radius = 4.5f, center = Offset(w * 0.75f, h * 0.85f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tide Timings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isHindi) "🌊 उच्च ज्वार: 04:15 PM (2.1m)" else "🌊 High Tide: 04:15 PM (2.1m)",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (isHindi) "भाटा: 10:30 PM (0.6m)" else "Low Tide: 10:30 PM (0.6m)",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}
