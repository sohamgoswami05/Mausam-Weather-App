package com.imd.mausam2.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imd.mausam2.data.model.PersonaType
import com.imd.mausam2.ui.cards.*
import com.imd.mausam2.ui.components.*
import com.imd.mausam2.ui.screens.AlertsScreen
import com.imd.mausam2.ui.screens.PersonasScreen
import com.imd.mausam2.ui.screens.RadarScreen
import com.imd.mausam2.ui.theme.SkyCyan
import com.imd.mausam2.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MausamDashboard(
    viewModel: MausamViewModel,
    onRequestGps: () -> Unit
) {
    val weatherData by viewModel.weatherData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val activePersonas by viewModel.activePersonas.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val isHindi by viewModel.isHindi.collectAsState()
    val isFahrenheit by viewModel.isFahrenheit.collectAsState()
    val wallpaperTheme by viewModel.wallpaperTheme.collectAsState()
    val weatherEffectsEnabled by viewModel.weatherEffectsEnabled.collectAsState()
    val smartGlanceText by viewModel.smartGlanceText.collectAsState()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val showOnboarding by viewModel.showOnboarding.collectAsState()

    var showCitySheet by remember { mutableStateOf(false) }
    var showCustomizerDialog by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    val currentCode = weatherData?.weatherCode ?: 1
    val currentTime = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    // Hardware / System Gesture Back Button Handling: return to main weather interface
    BackHandler(enabled = currentTab != NavTab.HOME) {
        viewModel.selectTab(NavTab.HOME)
    }

    AtmosphericBackground(
        weatherCode = currentCode,
        wallpaperTheme = wallpaperTheme,
        weatherEffectsEnabled = weatherEffectsEnabled
    ) {
        if (weatherData == null && isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = SkyCyan)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (isHindi) "मौसम डेटा सिंक हो रहा है..." else "Loading Mausam 2.0 Dashboard...",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            weatherData?.let { data ->
                Scaffold(
                    containerColor = Color.Transparent,
                    bottomBar = {
                        FloatingActionDock(
                            onLocationClick = { showCitySheet = true },
                            onCustomizeClick = { showCustomizerDialog = true },
                            onSettingsClick = { showSettingsSheet = true },
                            onRefreshClick = {
                                viewModel.loadWeather(data.location, forceRefresh = true)
                            },
                            currentTab = currentTab,
                            onHomeClick = { viewModel.selectTab(NavTab.HOME) },
                            isHindi = isHindi,
                            isLoading = isLoading
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        when (currentTab) {
                            NavTab.HOME -> {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .statusBarsPadding(),
                                    contentPadding = PaddingValues(bottom = 24.dp)
                                ) {
                                    // 1. TOP SCROLLING CRITICAL WEATHER UPDATES BAR
                                    item {
                                        CriticalAlertsTicker(
                                            data = data,
                                            isHindi = isHindi,
                                            onAlertClick = { viewModel.selectTab(NavTab.ALERTS) }
                                        )
                                    }

                                    // 2. HERO WEATHER CARD (Redesigned with modern aesthetic)
                                    item {
                                        HeroWeatherCard(
                                            data = data,
                                            isHindi = isHindi,
                                            isFahrenheit = isFahrenheit,
                                            smartGlanceText = smartGlanceText,
                                            isPlayingAudio = isPlayingAudio,
                                            onAudioToggle = { viewModel.toggleAudio() },
                                            onLocationClick = { showCitySheet = true },
                                            onCustomizeClick = { viewModel.triggerOnboarding() },
                                            onLanguageToggle = { viewModel.toggleLanguage() }
                                        )
                                    }

                                    // 2.5 MAUSAM 2.0 CORE FEATURES QUICK ACCESS HUB
                                    item {
                                        CoreFeaturesQuickHub(
                                            isHindi = isHindi,
                                            onRadarClick = { viewModel.selectTab(NavTab.RADAR) },
                                            onAlertsClick = { viewModel.selectTab(NavTab.ALERTS) },
                                            onAssistantClick = { viewModel.selectTab(NavTab.PERSONAS) },
                                            onWizardClick = { viewModel.triggerOnboarding() }
                                        )
                                    }

                                    item {
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }

                                    // 3. MODERN INSIGHT CARDS (Comfort, Packing, UV, Air Quality, Damini, Agromet)

                                    // Card A: Comfort Index
                                    item {
                                        val comfortLevel = when {
                                            data.feelsLike >= 42 -> Pair(if (isHindi) "अत्यधिक गर्म" else "Very hot", "• Watch")
                                            data.feelsLike >= 36 -> Pair(if (isHindi) "गर्म" else "Hot", "• Caution")
                                            data.feelsLike >= 28 -> Pair(if (isHindi) "सुहावना" else "Pleasant", "• Normal")
                                            data.feelsLike >= 18 -> Pair(if (isHindi) "हल्का ठंडा" else "Mild", "• Good")
                                            else -> Pair(if (isHindi) "ठंडा" else "Cold", "• Chilly")
                                        }
                                        val feelsStr = com.imd.mausam2.domain.WeatherUnitHelper.formatTemp(data.feelsLike, isFahrenheit)

                                        MausamInsightCard(
                                            categoryTitle = if (isHindi) "सुगमता सूचकांक" else "Comfort index",
                                            headline = comfortLevel.first,
                                            statusBadgeText = comfortLevel.second,
                                            statusBadgeBg = Color(0xFF5A3C26),
                                            statusBadgeColor = Color(0xFFFFB74D),
                                            subDetail = if (isHindi) "महसूस: $feelsStr" else "Feels like $feelsStr",
                                            footerAttribution = getObservatoryName(data.location.cityName),
                                            icon = Icons.Default.Eco,
                                            iconBgColor = Color(0xFF4A3425),
                                            iconTintColor = Color(0xFFF5D6C6)
                                        )
                                    }

                                    // Card B: Packing Tip
                                    item {
                                        val destination = if (data.location.cityName.contains("Delhi", true)) "For Kolkata" else "For New Delhi"
                                        MausamInsightCard(
                                            categoryTitle = if (isHindi) "पैकिंग सुझाव" else "Packing tip",
                                            headline = if (isHindi) data.packingAdviceHi else data.packingAdvice,
                                            subDetail = destination,
                                            footerAttribution = "Weather data by Open-Meteo.com • as of $currentTime",
                                            icon = Icons.Default.Luggage,
                                            iconBgColor = Color(0xFF383152),
                                            iconTintColor = Color(0xFFDDD6FE)
                                        )
                                    }

                                    // Card C: UV Index
                                    item {
                                        val uvBadge = when {
                                            data.uvIndex >= 8 -> Pair("• Very High", Color(0xFFEF4444))
                                            data.uvIndex >= 6 -> Pair("• High", Color(0xFFFBBF24))
                                            data.uvIndex >= 3 -> Pair("• Moderate", Color(0xFFF59E0B))
                                            else -> Pair("• Low", Color(0xFF4ADE80))
                                        }

                                        MausamInsightCard(
                                            categoryTitle = if (isHindi) "पराबैंगनी सूचकांक" else "UV index",
                                            headline = "${data.uvIndex.toInt()}",
                                            statusBadgeText = uvBadge.first,
                                            statusBadgeBg = Color(0xFF4A3C1A),
                                            statusBadgeColor = uvBadge.second,
                                            subDetail = if (data.uvIndex >= 6) "High • SPF 30+ recommended" else "Safe exposure window",
                                            footerAttribution = "Weather data by Open-Meteo.com • as of $currentTime",
                                            icon = Icons.Default.WbSunny,
                                            iconBgColor = Color(0xFF4A3C1A),
                                            iconTintColor = Color(0xFFFEF08A),
                                            customIcon = {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.WbSunny,
                                                        contentDescription = "UV",
                                                        tint = Color(0xFFFEF08A),
                                                        modifier = Modifier.size(30.dp)
                                                    )
                                                    Text(
                                                        text = "UV",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = Color(0xFF3A2800),
                                                        letterSpacing = (-0.5).sp
                                                    )
                                                }
                                            }
                                        )
                                    }

                                    // Card D: Air Quality Index (AQI)
                                    item {
                                        val aqiBadge = when {
                                            data.aqi > 200 -> Triple("• Severe", Color(0xFFF87171), Color(0xFF451A1A))
                                            data.aqi > 100 -> Triple("• Moderate", Color(0xFFFBBF24), Color(0xFF422B14))
                                            else -> Triple("• Good", Color(0xFF4ADE80), Color(0xFF133827))
                                        }

                                        MausamInsightCard(
                                            categoryTitle = if (isHindi) "वायु गुणवत्ता सूचकांक" else "Air quality",
                                            headline = "${data.aqi}",
                                            statusBadgeText = aqiBadge.first,
                                            statusBadgeBg = aqiBadge.third,
                                            statusBadgeColor = aqiBadge.second,
                                            subDetail = "PM2.5: ${data.pm25.toInt()} µg/m³ • PM10: ${data.pm10.toInt()} µg/m³",
                                            footerAttribution = "Central Pollution Control Board (CPCB) • IMD",
                                            icon = Icons.Default.Air,
                                            iconBgColor = Color(0xFF1B3D2F),
                                            iconTintColor = Color(0xFFA7F3D0)
                                        )
                                    }

                                    // Card E: Damini Lightning & Radar Warning
                                    item {
                                        val strike = data.lightningProximityKm
                                        MausamInsightCard(
                                            categoryTitle = if (isHindi) "दामिनी वज्रपात संवेदक" else "Lightning & Radar",
                                            headline = if (strike != null) "Lightning nearby ($strike km)" else "No strikes nearby",
                                            statusBadgeText = if (strike != null) "• Alert" else "• Safe",
                                            statusBadgeBg = if (strike != null) Color(0xFF451A1A) else Color(0xFF133827),
                                            statusBadgeColor = if (strike != null) Color(0xFFF87171) else Color(0xFF4ADE80),
                                            subDetail = if (strike != null) "Precautionary shelter advised" else "Nearest strike > 25 km away",
                                            footerAttribution = "IITM-Pune Damini Sensor Grid • 10-min scan",
                                            icon = Icons.Default.ElectricBolt,
                                            iconBgColor = Color(0xFF451A1A),
                                            iconTintColor = Color(0xFFFCA5A5),
                                            onClick = { viewModel.selectTab(NavTab.RADAR) }
                                        )
                                    }

                                    // Card F: Kisan Agromet Advisory
                                    item {
                                        MausamInsightCard(
                                            categoryTitle = if (isHindi) "किसान मौसम सलाह" else "Agromet Advisory",
                                            headline = if (data.pesticideSpraySafe) "Spray Safe Window" else "Delay Chemical Spraying",
                                            statusBadgeText = if (data.pesticideSpraySafe) "• Favorable" else "• Caution",
                                            statusBadgeBg = if (data.pesticideSpraySafe) Color(0xFF133827) else Color(0xFF422B14),
                                            statusBadgeColor = if (data.pesticideSpraySafe) Color(0xFF4ADE80) else Color(0xFFFBBF24),
                                            subDetail = data.sprayAdvice,
                                            footerAttribution = "IMD Agromet Field Unit (AMFU) • MoES",
                                            icon = Icons.Default.Grass,
                                            iconBgColor = Color(0xFF233E2B),
                                            iconTintColor = Color(0xFFBBF7D0)
                                        )
                                    }

                                    // 4. 7-DAY FORECAST HORIZONTAL RIBBON
                                    item {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        SevenDayForecastRow(
                                            days = data.sevenDayForecast,
                                            isHindi = isHindi,
                                            isFahrenheit = isFahrenheit
                                        )
                                    }

                                    // 5. PERSONA QUICK SELECTOR TABS
                                    item {
                                        PersonaQuickSelector(
                                            activePersonas = activePersonas,
                                            selectedFilter = selectedFilter,
                                            onFilterSelected = { viewModel.setFilter(it) },
                                            isHindi = isHindi
                                        )
                                    }

                                    // 6. DYNAMIC PERSONALIZED CARDS FEED
                                    val displayedPersonas = if (selectedFilter != null) {
                                        listOf(selectedFilter!!)
                                    } else {
                                        PersonaType.values().filter { activePersonas.contains(it) }
                                    }

                                    items(displayedPersonas.size) { index ->
                                        when (displayedPersonas[index]) {
                                            PersonaType.HEALTH -> HealthCard(data = data, isHindi = isHindi)
                                            PersonaType.FITNESS -> FitnessCard(data = data, isHindi = isHindi)
                                            PersonaType.BEACH -> BeachCard(data = data, isHindi = isHindi)
                                            PersonaType.AGRICULTURE -> AgricultureCard(data = data, isHindi = isHindi)
                                            PersonaType.COMMUTE -> CommuterCard(data = data, isHindi = isHindi)
                                            PersonaType.PARENTS -> CommuterCard(data = data, isHindi = isHindi)
                                            PersonaType.TRAVEL -> TravelCard(data = data, isHindi = isHindi)
                                            PersonaType.SEVERE -> SevereWeatherCard(data = data, isHindi = isHindi)
                                        }
                                    }

                                    // 7. OFFICIAL FOOTER
                                    item {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 20.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "India Meteorological Department (IMD) • Ministry of Earth Sciences",
                                                color = TextMuted,
                                                fontSize = 11.sp,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Mausam 2.0 • Smart India Hackathon (SIH-26076)",
                                                color = SkyCyan.copy(alpha = 0.8f),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }

                            NavTab.RADAR -> {
                                RadarScreen(
                                    data = data,
                                    isHindi = isHindi,
                                    onBack = { viewModel.selectTab(NavTab.HOME) }
                                )
                            }

                            NavTab.ALERTS -> {
                                AlertsScreen(
                                    data = data,
                                    isHindi = isHindi,
                                    onBack = { viewModel.selectTab(NavTab.HOME) }
                                )
                            }

                            NavTab.PERSONAS -> {
                                PersonasScreen(
                                    data = data,
                                    activePersonas = activePersonas,
                                    isHindi = isHindi,
                                    viewModel = viewModel,
                                    onOpenOnboarding = { viewModel.triggerOnboarding() },
                                    onBack = { viewModel.selectTab(NavTab.HOME) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 1. FIRST-TIME OR MANUAL ONBOARDING FULL-SCREEN OVERLAY
    AnimatedVisibility(
        visible = showOnboarding,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        OnboardingDialog(
            initialSelected = activePersonas,
            isHindi = isHindi,
            onDismiss = { viewModel.dismissOnboarding() },
            onComplete = { selectedSet ->
                viewModel.completeOnboarding(selectedSet)
            }
        )
    }

    // 2. City Switcher Modal Sheet
    if (showCitySheet) {
        CitySwitcherSheet(
            currentCity = weatherData?.location?.cityName ?: "New Delhi",
            isHindi = isHindi,
            onDismiss = { showCitySheet = false },
            onCitySelected = { loc -> viewModel.loadWeather(loc) },
            onRequestGps = { onRequestGps() }
        )
    }

    // 3. Card Customizer Priority Dialog
    if (showCustomizerDialog) {
        CardCustomizerDialog(
            currentPersonas = activePersonas,
            isHindi = isHindi,
            onDismiss = { showCustomizerDialog = false },
            onSave = { newSet -> viewModel.updateActivePersonas(newSet) }
        )
    }

    // 4. Settings Screen Full-Screen Dialog
    if (showSettingsSheet) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showSettingsSheet = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            com.imd.mausam2.ui.screens.MausamSettingsScreen(
                viewModel = viewModel,
                onBack = { showSettingsSheet = false }
            )
        }
    }
}

private fun getObservatoryName(cityName: String): String {
    val lower = cityName.lowercase()
    return when {
        lower.contains("kolkata") -> "IMD Kolkata-Alipore observation +"
        lower.contains("delhi") -> "IMD Safdarjung observation +"
        lower.contains("mumbai") -> "IMD Colaba-Santacruz observation +"
        lower.contains("chennai") -> "IMD Meenambakkam observation +"
        lower.contains("bengaluru") || lower.contains("bangalore") -> "IMD Bengaluru-HAL observation +"
        lower.contains("pune") -> "IMD Pune-Shivajinagar observation +"
        else -> "IMD $cityName observation +"
    }
}

