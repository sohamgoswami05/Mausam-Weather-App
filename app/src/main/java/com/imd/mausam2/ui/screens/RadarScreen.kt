package com.imd.mausam2.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imd.mausam2.data.model.CompleteWeatherData
import com.imd.mausam2.ui.components.GlassCard
import com.imd.mausam2.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarScreen(data: CompleteWeatherData, isHindi: Boolean) {
    var selectedLayer by remember { mutableStateOf("Doppler Rain") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isHindi) "आईएमडी डॉपलर मौसम रडार" else "IMD Doppler Weather Radar",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${data.location.cityName} • Live Nowcast Sweep",
                    color = SkyCyan,
                    fontSize = 12.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0x334CAF50)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF4CAF50)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "LIVE", color = Color(0xFF4CAF50), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Interactive Animated Radar Viewport
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            backgroundAlpha = 0.4f
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "radarRotation")
            val angle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "radarAngle"
            )

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(260.dp)) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2

                    // Concentric Radar Rings (Range 50km, 100km, 150km, 200km)
                    for (i in 1..4) {
                        drawCircle(
                            color = Color(0x3300E5FF),
                            radius = radius * (i / 4f),
                            center = center,
                            style = Stroke(width = 1.5f)
                        )
                    }

                    // Crosshairs
                    drawLine(
                        color = Color(0x3300E5FF),
                        start = Offset(center.x, 0f),
                        end = Offset(center.x, size.height),
                        strokeWidth = 1.5f
                    )
                    drawLine(
                        color = Color(0x3300E5FF),
                        start = Offset(0f, center.y),
                        end = Offset(size.width, center.y),
                        strokeWidth = 1.5f
                    )

                    // Simulated Weather Cloud Clutter & Precipitation Echoes
                    drawCircle(
                        color = Color(0x5500B4D8),
                        radius = 35f,
                        center = Offset(center.x + 40f, center.y - 30f)
                    )
                    drawCircle(
                        color = Color(0x66FFD166),
                        radius = 22f,
                        center = Offset(center.x + 45f, center.y - 25f)
                    )
                    drawCircle(
                        color = Color(0x77E63946),
                        radius = 12f,
                        center = Offset(center.x + 48f, center.y - 22f)
                    )

                    // Rotating Radar Sweep Beam
                    val rad = Math.toRadians(angle.toDouble())
                    val endX = center.x + radius * cos(rad).toFloat()
                    val endY = center.y + radius * sin(rad).toFloat()

                    drawLine(
                        brush = Brush.radialGradient(
                            colors = listOf(SkyCyan, Color.Transparent),
                            center = center,
                            radius = radius
                        ),
                        start = center,
                        end = Offset(endX, endY),
                        strokeWidth = 3f
                    )
                }

                // Range Tags
                Column(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "NW • 200km", color = TextMuted, fontSize = 10.sp)
                        Text(text = "NE • 200km", color = TextMuted, fontSize = 10.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "SW • 200km", color = TextMuted, fontSize = 10.sp)
                        Text(text = "SE • 200km", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Radar Legends
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = Color(0xFF00B4D8), label = if (isHindi) "हल्की वर्षा" else "Light Rain")
            LegendItem(color = Color(0xFFFFD166), label = if (isHindi) "मध्यम" else "Moderate")
            LegendItem(color = Color(0xFFE63946), label = if (isHindi) "भारी वर्षा" else "Heavy Rain")
            LegendItem(color = Color(0xFF9C27B0), label = if (isHindi) "ओलावृष्टि" else "Hail / Severe")
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Damini Lightning Sensor Card
        GlassCard(modifier = Modifier.fillMaxWidth(), backgroundAlpha = 0.25f) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFFFD600).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.ElectricBolt, contentDescription = null, tint = Color(0xFFFFD600), modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isHindi) "दामिनी वज्रपात अर्ली वॉर्निंग" else "Damini Lightning Early Detection",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isHindi) "आईआईटीम-पुणे एवं आईएमडी सेंसर ग्रिड से संयोजित" else "Connected to IITM-Pune & IMD lightning sensor network",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, color = TextMuted, fontSize = 10.sp)
    }
}
