package com.imd.mausam2.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imd.mausam2.data.model.PersonaType
import com.imd.mausam2.domain.WallpaperTheme
import com.imd.mausam2.ui.MausamViewModel
import com.imd.mausam2.ui.components.NavTab
import com.imd.mausam2.ui.theme.SkyCyan
import com.imd.mausam2.ui.theme.SunGold

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MausamSettingsScreen(
    viewModel: MausamViewModel,
    onBack: () -> Unit
) {
    val isHindi by viewModel.isHindi.collectAsState()
    val isFahrenheit by viewModel.isFahrenheit.collectAsState()
    val activePersonas by viewModel.activePersonas.collectAsState()
    val wallpaperTheme by viewModel.wallpaperTheme.collectAsState()
    val weatherEffectsEnabled by viewModel.weatherEffectsEnabled.collectAsState()
    val largeTextEnabled by viewModel.largeTextEnabled.collectAsState()
    val audioLanguageHindi by viewModel.audioLanguageHindi.collectAsState()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsState()

    var wallpaperColorsOn by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isHindi) "सेटिंग्स (Settings)" else "Settings",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ==========================================
            // 1. PERSONAS SECTION (Compass Squircle)
            // ==========================================
            SettingsSectionHeader(
                icon = Icons.Default.Explore,
                iconBg = Color(0xFF38BDF8),
                title = if (isHindi) "उपभोक्ता प्रोफाइल (Personas)" else "Personas",
                subtitle = if (isHindi) "होम पेज के कार्ड इन्हीं प्राथमिकताओं से आते हैं" else "Cards on your home page come from these"
            )

            SettingsCardContainer {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PersonaType.values().forEach { persona ->
                        val isSelected = activePersonas.contains(persona)
                        val icon = when (persona) {
                            PersonaType.HEALTH -> Icons.Default.Favorite
                            PersonaType.FITNESS -> Icons.Default.DirectionsRun
                            PersonaType.BEACH -> Icons.Default.Pool
                            PersonaType.TRAVEL -> Icons.Default.Flight
                            PersonaType.PARENTS -> Icons.Default.FamilyRestroom
                            PersonaType.AGRICULTURE -> Icons.Default.Agriculture
                            PersonaType.COMMUTE -> Icons.Default.DirectionsCar
                            PersonaType.SEVERE -> Icons.Default.ElectricBolt
                        }
                        val title = if (isHindi) persona.titleHi else persona.titleEn

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFF334155) else Color(0xFF1E293B).copy(alpha = 0.6f),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) SkyCyan.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f)
                            ),
                            modifier = Modifier.clickable {
                                viewModel.toggleActivePersona(persona)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) SkyCyan else Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = title,
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 2. APPEARANCE SECTION (Palette Squircle)
            // ==========================================
            SettingsSectionHeader(
                icon = Icons.Default.Palette,
                iconBg = Color(0xFFA855F7),
                title = if (isHindi) "उपस्थिति एवं रंग (Appearance)" else "Appearance"
            )

            SettingsCardContainer {
                // Wallpaper colors toggle row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "वॉलपेपर रंग (Wallpaper colours)" else "Wallpaper colours",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isHindi) "थीम पैलेट चुनें" else "Material You palette from your wallpaper",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = wallpaperColorsOn,
                        onCheckedChange = { wallpaperColorsOn = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF38BDF8)
                        )
                    )
                }

                if (wallpaperColorsOn) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // 6 Color Dots Row
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            WallpaperTheme.values().forEach { theme ->
                                val isSelected = wallpaperTheme == theme
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(theme.dotColor)
                                        .clickable { viewModel.setWallpaperTheme(theme) }
                                        .then(
                                            if (isSelected) Modifier.border(2.5.dp, Color.White, CircleShape)
                                            else Modifier
                                        )
                                )
                            }
                        }
                        Text(
                            text = if (isHindi) wallpaperTheme.titleHi else wallpaperTheme.titleEn,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 14.dp),
                    color = Color.White.copy(alpha = 0.08f)
                )

                // Weather effects toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "मौसम प्रभाव (Weather effects)" else "Weather effects",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isHindi) "एनिमेटेड आकाश, बादल, चमक और बारिश" else "Animated sky, clouds and rain • glass blur on",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = weatherEffectsEnabled,
                        onCheckedChange = { viewModel.toggleWeatherEffects() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF38BDF8)
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 14.dp),
                    color = Color.White.copy(alpha = 0.08f)
                )

                // Large text toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "बड़ा टेक्स्ट (Large text)" else "Large text",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isHindi) "एकल कॉलम, बड़े मान, सघन ग्लास" else "Single column, bigger values, denser glass",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = largeTextEnabled,
                        onCheckedChange = { viewModel.toggleLargeText() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF38BDF8)
                        )
                    )
                }
            }

            // ==========================================
            // 3. UNITS SECTION (Ruler / Measure Squircle)
            // ==========================================
            SettingsSectionHeader(
                icon = Icons.Default.Straighten,
                iconBg = Color(0xFFFB923C),
                title = if (isHindi) "इकाइयाँ (Units)" else "Units"
            )

            SettingsCardContainer {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                        .padding(4.dp)
                ) {
                    // Celsius Pill
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (!isFahrenheit) Color(0xFF334155) else Color.Transparent)
                            .clickable { viewModel.setUnit(false) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (!isFahrenheit) "✓  °C · km/h" else "°C · km/h",
                            color = if (!isFahrenheit) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            fontWeight = if (!isFahrenheit) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    // Fahrenheit Pill
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isFahrenheit) Color(0xFF334155) else Color.Transparent)
                            .clickable { viewModel.setUnit(true) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isFahrenheit) "✓  °F · mph" else "°F · mph",
                            color = if (isFahrenheit) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            fontWeight = if (isFahrenheit) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // ==========================================
            // 4. LANGUAGE SECTION (Globe Squircle)
            // ==========================================
            SettingsSectionHeader(
                icon = Icons.Default.Language,
                iconBg = Color(0xFF4ADE80),
                title = if (isHindi) "भाषा (Language)" else "Language"
            )

            SettingsCardContainer {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                        .padding(4.dp)
                ) {
                    // English Pill
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (!isHindi) Color(0xFF334155) else Color.Transparent)
                            .clickable { viewModel.setLanguage(false) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (!isHindi) "✓  English" else "English",
                            color = if (!isHindi) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            fontWeight = if (!isHindi) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    // Hindi Pill
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isHindi) Color(0xFF334155) else Color.Transparent)
                            .clickable { viewModel.setLanguage(true) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isHindi) "✓  हिन्दी" else "हिन्दी",
                            color = if (isHindi) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            fontWeight = if (isHindi) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // ==========================================
            // 5. AUDIO SPEECH BRIEFING (Neural Voice)
            // ==========================================
            SettingsSectionHeader(
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                iconBg = Color(0xFFF59E0B),
                title = if (isHindi) "ऑडियो मौसम रिपोर्ट (Weather Speech)" else "Weather Speech (Neural Voice)",
                subtitle = if (isHindi) "अपनी पसंदीदा भाषा में मौसम रिपोर्ट सुनें" else "Listen to the live weather briefing in English or Hindi"
            )

            SettingsCardContainer {
                // Voice language selector
                Text(
                    text = if (isHindi) "आवाज़ की भाषा चुनें" else "Voice Language",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (!audioLanguageHindi) Color(0xFF334155) else Color.Transparent)
                            .clickable { viewModel.setAudioLanguage(false) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (!audioLanguageHindi) "✓  English Voice" else "English Voice",
                            color = if (!audioLanguageHindi) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = 13.sp,
                            fontWeight = if (!audioLanguageHindi) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (audioLanguageHindi) Color(0xFF334155) else Color.Transparent)
                            .clickable { viewModel.setAudioLanguage(true) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (audioLanguageHindi) "✓  हिन्दी आवाज़" else "हिन्दी आवाज़",
                            color = if (audioLanguageHindi) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = 13.sp,
                            fontWeight = if (audioLanguageHindi) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action buttons: Preview in English and Preview in Hindi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.playBriefing(false) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlayingAudio && !audioLanguageHindi) Color(0xFFF59E0B) else Color(0xFF334155)
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Listen English", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.playBriefing(true) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlayingAudio && audioLanguageHindi) Color(0xFFF59E0B) else Color(0xFF334155)
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "हिन्दी में सुनें", fontSize = 12.sp)
                    }
                }
            }

            // ==========================================
            // 6. DAILY BRIEFS & SCHEDULES (Bell, Moon, Bus)
            // ==========================================
            SettingsSectionHeader(
                icon = Icons.Default.NotificationsActive,
                iconBg = Color(0xFFFACC15),
                title = if (isHindi) "दैनिक बुलेटिन (Daily briefs)" else "Daily briefs",
                subtitle = if (isHindi) "आपके व्यक्तित्व के लिए सुबह और शाम का सारांश" else "A morning and evening note for your persona"
            )

            SettingsCardContainer {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (isHindi) "सुबह का बुलेटिन" else "Morning brief", color = Color.White, fontSize = 14.sp)
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF334155)) {
                        Text(text = "07:00", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (isHindi) "शाम का बुलेटिन" else "Evening brief", color = Color.White, fontSize = 14.sp)
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF334155)) {
                        Text(text = "18:00", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.playBriefing(isHindi) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                    ) {
                        Text(text = if (isHindi) "सुबह का पूर्वावलोकन" else "Preview morning", fontSize = 11.sp)
                    }
                    Button(
                        onClick = { viewModel.playBriefing(isHindi) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                    ) {
                        Text(text = if (isHindi) "शाम का पूर्वावलोकन" else "Preview evening", fontSize = 11.sp)
                    }
                }
            }

            // Quiet hours & Commute Windows
            SettingsSectionHeader(
                icon = Icons.Default.Bedtime,
                iconBg = Color(0xFF818CF8),
                title = if (isHindi) "शांत समय (Quiet hours)" else "Quiet hours",
                subtitle = if (isHindi) "बुलेटिन मौन रहते हैं; नारंगी और लाल अलर्ट आते हैं" else "Briefs stay silent; orange and red alerts still come through"
            )

            SettingsCardContainer {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (isHindi) "प्रारंभ" else "Start", color = Color.White, fontSize = 14.sp)
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF334155)) {
                        Text(text = "22:00", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (isHindi) "समाप्त" else "End", color = Color.White, fontSize = 14.sp)
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF334155)) {
                        Text(text = "06:30", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
                    }
                }
            }

            // Commute Window
            SettingsSectionHeader(
                icon = Icons.Default.DirectionsBus,
                iconBg = Color(0xFF2DD4BF),
                title = if (isHindi) "दैनिक यात्रा (Commute)" else "Commute",
                subtitle = if (isHindi) "जल्दी निकलने के सुझाव इस समय में काम करते हैं" else "Leave-earlier nudges use this window"
            )

            SettingsCardContainer {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (isHindi) "यात्रा प्रारंभ" else "Commute starts", color = Color.White, fontSize = 14.sp)
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF334155)) {
                        Text(text = "08:00", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (isHindi) "यात्रा समाप्त" else "Commute ends", color = Color.White, fontSize = 14.sp)
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF334155)) {
                        Text(text = "10:00", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
                    }
                }
            }

            // ==========================================
            // 7. PRESERVED ADVANCED IMD FEATURES
            // ==========================================
            SettingsSectionHeader(
                icon = Icons.Default.DashboardCustomize,
                iconBg = SkyCyan,
                title = if (isHindi) "मौसम 2.0 मुख्य सुविधाएं" else "Mausam 2.0 Core Features",
                subtitle = if (isHindi) "इसरो रडार, आईएमडी चेतावनियां एवं 7-दिवसीय AI चैटबॉट" else "Radar, IMD Warning Alerts & Conversational Assistant"
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SettingsFeatureCard(
                    title = if (isHindi) "इसरो मोसडैक एवं आईएमडी रडार" else "ISRO MOSDAC & IMD Doppler Radar",
                    subtitle = if (isHindi) "लाइव डॉपलर अबकास्ट, इन्सेट-3DR एवं दामिनी ग्रिड" else "Live DWR nowcast, INSAT-3DR GIS & lightning grid",
                    icon = Icons.Default.Radar,
                    iconTint = SkyCyan,
                    onClick = {
                        viewModel.selectTab(NavTab.RADAR)
                        onBack()
                    }
                )

                SettingsFeatureCard(
                    title = if (isHindi) "आईएमडी 4-स्तरीय चेतावनी अलर्ट" else "IMD 4-Stage Warning Alerts",
                    subtitle = if (isHindi) "लाल, नारंगी एवं पीली आधिकारिक मौसम चेतावनियां" else "Red, orange & yellow regional advisories",
                    icon = Icons.Default.NotificationsActive,
                    iconTint = Color(0xFFF87171),
                    onClick = {
                        viewModel.selectTab(NavTab.ALERTS)
                        onBack()
                    }
                )

                SettingsFeatureCard(
                    title = if (isHindi) "प्रोफाइल एवं 7-दिवसीय AI चैटबॉट" else "Profiles & 7-Day AI Chatbot",
                    subtitle = if (isHindi) "मौसम प्रश्नोत्तरी एवं सात दिवसीय एआई विश्लेषण" else "Ask questions about next 7 days in Kolkata/Delhi",
                    icon = Icons.Default.SmartToy,
                    iconTint = Color(0xFFA855F7),
                    onClick = {
                        viewModel.selectTab(NavTab.PERSONAS)
                        onBack()
                    }
                )

                SettingsFeatureCard(
                    title = if (isHindi) "ऑनबोर्डिंग विज़ार्ड पुनः चलाएं" else "Re-run Persona Onboarding Wizard",
                    subtitle = if (isHindi) "प्रारंभिक सेटअप और कार्ड अनुकूलन" else "Reset and re-configure your personas",
                    icon = Icons.Default.Tune,
                    iconTint = SunGold,
                    onClick = {
                        viewModel.triggerOnboarding()
                        onBack()
                    }
                )
            }

            // ==========================================
            // 8. PRIVACY & CREDITS (Shield Squircle)
            // ==========================================
            SettingsSectionHeader(
                icon = Icons.Default.Security,
                iconBg = Color(0xFF22C55E),
                title = if (isHindi) "गोपनीयता एवं श्रेय (Privacy & credits)" else "Privacy & credits"
            )

            SettingsCardContainer {
                Text(
                    text = if (isHindi)
                        "आपका उपयोग डेटा कभी इस फ़ोन से बाहर नहीं जाता। कार्ड प्राथमिकताएं, टैप और सेटिंग्स केवल इस डिवाइस पर सुरक्षित रहती हैं।"
                    else
                        "Your usage never leaves this phone. Card ranking, taps and preferences are stored only on this device.",
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Data: India Meteorological Department (IMD), NDMA SACHET, CPCB via data.gov.in, Weather data by Open-Meteo.com (CC BY 4.0).\n\nBuilt for Smart India Hackathon (SIH) 2024 / 2026. Font: Roboto Flex (OFL).",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF0F172A),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun SettingsCardContainer(
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF1B2433).copy(alpha = 0.85f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun SettingsFeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF1B2433).copy(alpha = 0.85f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
