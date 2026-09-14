package com.imd.mausam2.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.imd.mausam2.data.model.PersonaType
import com.imd.mausam2.ui.MausamViewModel
import com.imd.mausam2.ui.components.GlassCard
import com.imd.mausam2.ui.components.MausamChatbotFab
import com.imd.mausam2.ui.components.MausamChatbotModal
import com.imd.mausam2.ui.components.ThunderstormCloudSymbol
import com.imd.mausam2.ui.theme.*

@Composable
fun PersonasScreen(
    data: CompleteWeatherData,
    activePersonas: Set<PersonaType>,
    isHindi: Boolean,
    viewModel: MausamViewModel,
    onOpenOnboarding: () -> Unit,
    onBack: () -> Unit = {}
) {
    var selectedTravelCity by remember { mutableStateOf("London") }
    var showChatbot by remember { mutableStateOf(false) }

    val travelPackingMap = remember {
        mapOf(
            "London" to listOf("Carry a sturdy raincoat", "Pack compact umbrella", "Light waterproof jacket", "Water-resistant shoes"),
            "Goa" to listOf("Pack sunscreen SPF 50", "UV polarized sunglasses", "Breathable cotton clothing", "Beachwear & flip-flops"),
            "Shimla" to listOf("Heavy woolens & fleece jacket", "Thermal inner wear", "Insulated gloves & beanie", "Moisturizing skin balm"),
            "Mumbai" to listOf("Waterproof backpack cover", "Sturdy windproof umbrella", "Quick-dry synthetic fabrics", "Non-slip footwear"),
            "New Delhi" to listOf("N95 anti-pollution mask", "Air purifying nasal filter", "Light summer cottons", "Hydration water bottle")
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header with Top-Left Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Main",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isHindi) "उपभोक्ता प्रोफाइल एवं AI टूल्स" else "Consumer Personas & AI Tools",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isHindi) "अपनी दैनिक आवश्यकताओं के अनुसार प्रोफाइल अनुकूलित करें" else "Tailor your weather dashboard to your exact daily routine",
                        color = SkyCyan,
                        fontSize = 11.sp
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // 0. FEATURED: MAUSAM 7-DAY AI CHATBOT HERO CARD
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showChatbot = true },
                        backgroundAlpha = 0.35f,
                        cornerRadius = 20.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Brush.radialGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A))))
                                    .border(1.5.dp, Brush.linearGradient(listOf(SkyCyan, NeonOrange)), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                ThunderstormCloudSymbol(sizeDp = 28)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isHindi) "मौसम 7-दिवसीय AI चैटबॉट" else "Mausam 7-Day AI Chatbot",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SkyCyan.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "AI BOT",
                                            color = SkyCyan,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isHindi) "आगामी 7 दिनों के मौसम (बारिश, तापमान, तूफान) पर लाइव बातचीत करें"
                                           else "Ask questions about the upcoming 7 days (rain, heat, weekend)",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open Chatbot",
                                tint = SkyCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

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

    // Floating Circular AI Chatbot Button (clouds with thunderstorm symbol)
        MausamChatbotFab(
            onClick = { showChatbot = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp, end = 20.dp)
        )

        // Fullscreen / Modal 7-Day AI Chatbot Interface
        AnimatedVisibility(
            visible = showChatbot,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            MausamChatbotModal(
                data = data,
                isHindi = isHindi,
                viewModel = viewModel,
                onDismiss = { showChatbot = false }
            )
        }
    }
}
