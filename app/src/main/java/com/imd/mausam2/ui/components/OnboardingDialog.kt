package com.imd.mausam2.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.activity.compose.BackHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imd.mausam2.data.model.PersonaType
import com.imd.mausam2.ui.theme.*

data class PersonaCardInfo(
    val type: PersonaType,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconColor: Color,
    val titleEn: String,
    val titleHi: String,
    val subtitleEn: String,
    val subtitleHi: String
)

@Composable
fun OnboardingDialog(
    initialSelected: Set<PersonaType>,
    isHindi: Boolean,
    onDismiss: () -> Unit = {},
    onComplete: (Set<PersonaType>) -> Unit
) {
    var selectedPersonas by remember { mutableStateOf(initialSelected.toMutableSet()) }

    BackHandler {
        onDismiss()
    }

    val personaCards = remember {
        listOf(
            PersonaCardInfo(
                type = PersonaType.BEACH,
                icon = Icons.Default.Waves,
                iconColor = BrightTeal,
                titleEn = "Beachgoer & Coastal",
                titleHi = "तटीय एवं समुद्र",
                subtitleEn = "Tides, wave heights, surf safety flags & water temp",
                subtitleHi = "ज्वार-भाटा, लहर ऊंचाई, तटीय सुरक्षा ध्वज"
            ),
            PersonaCardInfo(
                type = PersonaType.FITNESS,
                icon = Icons.Default.DirectionsRun,
                iconColor = Color(0xFF00E676),
                titleEn = "Outdoor Fitness",
                titleHi = "आउटडोर फिटनेस",
                subtitleEn = "Run score, optimal workout hours & heat stress",
                subtitleHi = "दौड़ का समय, रन स्कोर, हीट स्ट्रेस अलर्ट"
            ),
            PersonaCardInfo(
                type = PersonaType.HEALTH,
                icon = Icons.Default.Favorite,
                iconColor = Color(0xFFFF4081),
                titleEn = "Health-Conscious",
                titleHi = "स्वास्थ्य एवं वायु",
                subtitleEn = "AQI, PM2.5, UV index, pollen & asthma risk",
                subtitleHi = "AQI, PM2.5, UV इंडेक्स, दमा एवं एलर्जी चेतावनी"
            ),
            PersonaCardInfo(
                type = PersonaType.AGRICULTURE,
                icon = Icons.Default.Yard,
                iconColor = Color(0xFF8BC34A),
                titleEn = "Farmer & Agri (Kisan)",
                titleHi = "किसान एवं कृषि",
                subtitleEn = "Soil moisture, pesticide spray window & frost alerts",
                subtitleHi = "मृदा नमी, कीटनाशक छिड़काव, पाला चेतावनी"
            ),
            PersonaCardInfo(
                type = PersonaType.COMMUTE,
                icon = Icons.Default.DirectionsCar,
                iconColor = Color(0xFF29B6F6),
                titleEn = "Daily Commuter",
                titleHi = "दैनिक यात्रा / कम्यूटर",
                subtitleEn = "Road visibility, fog distance & waterlogging risk",
                subtitleHi = "सड़क दृश्यता, कोहरा मीटर, जलभराव जोखिम"
            ),
            PersonaCardInfo(
                type = PersonaType.PARENTS,
                icon = Icons.Default.School,
                iconColor = SunGold,
                titleEn = "Parents & Family",
                titleHi = "स्कूल एवं परिवार",
                subtitleEn = "7–9 AM school commute conditions & sudden rain",
                subtitleHi = "सुबह 7-9 स्कूल कम्यूट एवं अचानक बारिश अलर्ट"
            ),
            PersonaCardInfo(
                type = PersonaType.TRAVEL,
                icon = Icons.Default.FlightTakeoff,
                iconColor = Color(0xFFCE93D8),
                titleEn = "Traveler & Packing",
                titleHi = "यात्री एवं टूरिस्ट",
                subtitleEn = "Smart packing suggestions & transit weather delays",
                subtitleHi = "स्मार्ट पैकिंग सहायता एवं फ्लाइट मौसम अलर्ट"
            ),
            PersonaCardInfo(
                type = PersonaType.SEVERE,
                icon = Icons.Default.ElectricBolt,
                iconColor = Color(0xFFFFD600),
                titleEn = "Severe Storm Chaser",
                titleHi = "वज्रपात एवं आंधी",
                subtitleEn = "Damini lightning radar & disaster helpline 112",
                subtitleHi = "दामिनी बिजली रडार एवं आपदा हेल्पलाइन 112"
            )
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF020617)))),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Header with Top-Left Back Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
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

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SkyCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DashboardCustomize,
                            contentDescription = "Personalize",
                            tint = SkyCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "मौसम 2.0 विज़ार्ड" else "Mausam 2.0 Wizard",
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isHindi) "कार्ड प्राथमिकताओं का चयन करें:" else "Select what matters most to your routine:",
                            color = SkyCyan,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isHindi) "चयनित कार्ड आपके होमपेज पर शीर्ष प्राथमिकताओं के रूप में प्रदर्शित होंगे।"
                           else "Tap any cards you want. Your homepage feed will dynamically re-prioritize for you.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            // 2-Column Grid of Scrollable Persona Cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                    items(personaCards) { card ->
                        val isSelected = selectedPersonas.contains(card.type)
                        val scale by animateFloatAsState(if (isSelected) 1.02f else 1.0f, label = "cardScale")

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (isSelected) SkyCyan.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.07f),
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) SkyCyan else Color.White.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(145.dp)
                                .scale(scale)
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    val newSet = selectedPersonas.toMutableSet()
                                    if (isSelected) {
                                        if (newSet.size > 1) newSet.remove(card.type)
                                    } else {
                                        newSet.add(card.type)
                                    }
                                    selectedPersonas = newSet
                                }
                        ) {
                            Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(card.iconColor.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = card.icon,
                                                contentDescription = card.titleEn,
                                                tint = card.iconColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        // Selection Checkmark
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(22.dp)
                                                .clip(CircleShape)
                                                .background(SkyCyan),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = Color.Black,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = if (isHindi) card.titleHi else card.titleEn,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )

                                    Spacer(modifier = Modifier.height(3.dp))

                                    Text(
                                        text = if (isHindi) card.subtitleHi else card.subtitleEn,
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        lineHeight = 13.sp,
                                        maxLines = 3
                                    )
                                }
                            }
                        }
                    }
                }

            // Pinned Bottom Action Bar with Navigation Insets
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xEE0F172A),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                shadowElevation = 16.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = { onComplete(selectedPersonas) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyCyan),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Text(
                            text = if (isHindi) "मेरा मौसम शुरू करें (${selectedPersonas.size} कार्ड चयनित) →"
                                   else "Launch My Personalized Mausam (${selectedPersonas.size} Cards) →",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
