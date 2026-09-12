package com.imd.mausam2.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.imd.mausam2.data.model.PersonaType
import com.imd.mausam2.ui.components.GlassCard
import com.imd.mausam2.ui.theme.*

@Composable
fun PersonasScreen(
    activePersonas: Set<PersonaType>,
    isHindi: Boolean,
    onOpenOnboarding: () -> Unit
) {
    var selectedTravelCity by remember { mutableStateOf("London") }

    val travelPackingMap = remember {
        mapOf(
            "London" to listOf("Carry a sturdy raincoat", "Pack compact umbrella", "Light waterproof jacket", "Water-resistant shoes"),
            "Goa" to listOf("Pack sunscreen SPF 50", "UV polarized sunglasses", "Breathable cotton clothing", "Beachwear & flip-flops"),
            "Shimla" to listOf("Heavy woolens & fleece jacket", "Thermal inner wear", "Insulated gloves & beanie", "Moisturizing skin balm"),
            "Mumbai" to listOf("Waterproof backpack cover", "Sturdy windproof umbrella", "Quick-dry synthetic fabrics", "Non-slip footwear"),
            "New Delhi" to listOf("N95 anti-pollution mask", "Air purifying nasal filter", "Light summer cottons", "Hydration water bottle")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Header
        Text(
            text = if (isHindi) "उपभोक्ता प्रोफाइल एवं स्मार्ट टूल्स" else "Consumer Personas & Tools",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (isHindi) "अपनी दैनिक आवश्यकताओं के अनुसार प्रोफाइल अनुकूलित करें" else "Tailor your weather dashboard to your exact daily routine",
            color = SkyCyan,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // 1. Current Active Personas & Re-customize Button
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isHindi) "सक्रिय कार्ड (${activePersonas.size})" else "Active Homepage Cards (${activePersonas.size})",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Button(
                                onClick = onOpenOnboarding,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SkyCyan),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isHindi) "कार्ड बदलें" else "Edit Cards",
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Active Persona Badges
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(activePersonas.toList()) { persona ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SkyCyan.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, SkyCyan.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = if (isHindi) persona.titleHi else persona.titleEn,
                                        color = SkyCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Traveler's Smart Packing Assistant (SIH Highlight Feature)
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFAB47BC).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Luggage, contentDescription = null, tint = Color(0xFFCE93D8), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "यात्री स्मार्ट पैकिंग विज़ार्ड" else "Traveler's Smart Packing Assistant",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isHindi) "गंतव्य के अनुसार पैकिंग सुझाव" else "Automated packing suggestions by destination weather",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // City Selection Chips
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(travelPackingMap.keys.toList()) { city ->
                                val isSelected = city == selectedTravelCity
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedTravelCity = city },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0xFFCE93D8).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f),
                                    border = BorderStroke(1.dp, if (isSelected) Color(0xFFCE93D8) else Color.White.copy(alpha = 0.15f))
                                ) {
                                    Text(
                                        text = city,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Packing Suggestions for Selected City
                        val itemsList = travelPackingMap[selectedTravelCity] ?: emptyList()
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            itemsList.forEach { item ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFFCE93D8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = item, color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Kisan & Agromet Seasonal Planting Guidance
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF8BC34A).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Agriculture, contentDescription = null, tint = Color(0xFF8BC34A), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "किसान मेघदूत मौसमी फसल सलाह" else "Kisan Meghdoot Seasonal Planting Guide",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "IMD Agromet Advisory Service (AAS)",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "🌾 खरीफ एवं रबी फसल सुरक्षा: वर्तमान आर्द्रता एवं मिट्टी की नमी को देखते हुए आगामी 48 घंटों में यूरिया टॉप-ड्रेसिंग करें। तेज हवा के समय छिड़काव से बचें।",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
