package com.imd.mausam2.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imd.mausam2.data.model.CompleteWeatherData
import com.imd.mausam2.data.model.LocationInfo
import com.imd.mausam2.data.model.PersonaType
import com.imd.mausam2.data.repository.WeatherRepository
import com.imd.mausam2.domain.MausamNeuralVoiceEngine
import com.imd.mausam2.domain.SmartGlanceAI
import com.imd.mausam2.ui.components.NavTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class MausamViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherRepository(application)
    private val prefs = application.getSharedPreferences("mausam2_user_prefs", Application.MODE_PRIVATE)
    private val voiceEngine = MausamNeuralVoiceEngine(application)

    private val _weatherData = MutableStateFlow<CompleteWeatherData?>(null)
    val weatherData: StateFlow<CompleteWeatherData?> = _weatherData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _activePersonas = MutableStateFlow<Set<PersonaType>>(loadSavedPersonas())
    val activePersonas: StateFlow<Set<PersonaType>> = _activePersonas.asStateFlow()

    private val _selectedFilter = MutableStateFlow<PersonaType?>(null)
    val selectedFilter: StateFlow<PersonaType?> = _selectedFilter.asStateFlow()

    private val _isHindi = MutableStateFlow(prefs.getBoolean("pref_is_hindi", false))
    val isHindi: StateFlow<Boolean> = _isHindi.asStateFlow()

    private val _smartGlanceText = MutableStateFlow("")
    val smartGlanceText: StateFlow<String> = _smartGlanceText.asStateFlow()

    val isPlayingAudio: StateFlow<Boolean> = voiceEngine.isPlaying

    // Tab Navigation State
    private val _currentTab = MutableStateFlow(NavTab.HOME)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    // Onboarding Dialog State (True if never completed or if user requests re-customizing)
    private val _showOnboarding = MutableStateFlow(!prefs.getBoolean("has_completed_onboarding", false))
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    init {
        voiceEngine.setLanguage(_isHindi.value)
        loadWeather(WeatherRepository.PRESET_CITIES.first())
    }

    fun loadWeather(location: LocationInfo, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getWeatherData(location, forceRefresh)
            result.onSuccess { data ->
                _weatherData.value = data
                updateSmartGlance(data)
            }
            _isLoading.value = false
        }
    }

    fun selectTab(tab: NavTab) {
        _currentTab.value = tab
    }

    fun triggerOnboarding() {
        _showOnboarding.value = true
    }

    fun completeOnboarding(newPersonas: Set<PersonaType>) {
        _activePersonas.value = newPersonas
        val ids = newPersonas.map { it.id }.toSet()
        prefs.edit()
            .putStringSet("pref_personas", ids)
            .putBoolean("has_completed_onboarding", true)
            .apply()
        _showOnboarding.value = false
        _weatherData.value?.let { updateSmartGlance(it) }
    }

    fun toggleLanguage() {
        val newLang = !_isHindi.value
        _isHindi.value = newLang
        prefs.edit().putBoolean("pref_is_hindi", newLang).apply()
        voiceEngine.setLanguage(newLang)
        _weatherData.value?.let { updateSmartGlance(it) }
    }

    fun updateActivePersonas(newSet: Set<PersonaType>) {
        _activePersonas.value = newSet
        val ids = newSet.map { it.id }.toSet()
        prefs.edit().putStringSet("pref_personas", ids).apply()
        _weatherData.value?.let { updateSmartGlance(it) }
    }

    fun setFilter(persona: PersonaType?) {
        _selectedFilter.value = persona
    }

    fun toggleAudio() {
        if (voiceEngine.isPlaying.value) {
            voiceEngine.stop()
        } else {
            val text = _smartGlanceText.value
            if (text.isNotBlank()) {
                voiceEngine.speak(text, _isHindi.value)
            }
        }
    }

    private fun updateSmartGlance(data: CompleteWeatherData) {
        val text = if (_isHindi.value) {
            SmartGlanceAI.generateInsightHi(data, _activePersonas.value)
        } else {
            SmartGlanceAI.generateInsightEn(data, _activePersonas.value)
        }
        _smartGlanceText.value = text
    }

    private fun loadSavedPersonas(): Set<PersonaType> {
        val saved = prefs.getStringSet("pref_personas", null)
        return if (saved != null) {
            PersonaType.values().filter { saved.contains(it.id) }.toSet()
        } else {
            // Default first-run personas: Beach, Fitness, Health, Agriculture, Commute
            setOf(
                PersonaType.BEACH,
                PersonaType.FITNESS,
                PersonaType.HEALTH,
                PersonaType.AGRICULTURE,
                PersonaType.COMMUTE
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceEngine.shutdown()
    }
}
