package com.imd.mausam2.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    val smartGlanceText by viewModel.smartGlanceText.collectAsState()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val showOnboarding by viewModel.showOnboarding.collectAsState()

    var showCitySheet by remember { mutableStateOf(false) }
    var showCustomizerDialog by remember { mutableStateOf(false) }

    val currentCode = weatherData?.weatherCode ?: 1

    AtmosphericBackground(weatherCode = currentCode) {
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
                        BottomNavBar(
                            currentTab = currentTab,
                            isHindi = isHindi,
                            onTabSelected = { viewModel.selectTab(it) }
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

                                    // 2. HERO WEATHER CARD
                                    item {
                                        HeroWeatherCard(
                                            data = data,
                                            isHindi = isHindi,
                                            smartGlanceText = smartGlanceText,
                                            isPlayingAudio = isPlayingAudio,
                                            onAudioToggle = { viewModel.toggleAudio() },
                                            onLocationClick = { showCitySheet = true },
                                            onCustomizeClick = { viewModel.triggerOnboarding() },
                                            onLanguageToggle = { viewModel.toggleLanguage() }
                                        )
                                    }

                                    // 3. 7-DAY FORECAST RIBBON
                                    item {
                                        SevenDayForecastRow(
                                            days = data.sevenDayForecast,
                                            isHindi = isHindi
                                        )
                                    }

                                    // 4. PERSONA QUICK SELECTOR TABS
                                    item {
                                        PersonaQuickSelector(
                                            activePersonas = activePersonas,
                                            selectedFilter = selectedFilter,
                                            onFilterSelected = { viewModel.setFilter(it) },
                                            isHindi = isHindi
                                        )
                                    }

                                    // 5. DYNAMIC PERSONALIZED CARDS FEED
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

                                    // 6. OFFICIAL FOOTER
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
                                RadarScreen(data = data, isHindi = isHindi)
                            }

                            NavTab.ALERTS -> {
                                AlertsScreen(data = data, isHindi = isHindi)
                            }

                            NavTab.PERSONAS -> {
                                PersonasScreen(
                                    activePersonas = activePersonas,
                                    isHindi = isHindi,
                                    onOpenOnboarding = { viewModel.triggerOnboarding() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 1. FIRST-TIME OR MANUAL ONBOARDING POP-UP DIALOG
    if (showOnboarding) {
        OnboardingDialog(
            initialSelected = activePersonas,
            isHindi = isHindi,
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
}
