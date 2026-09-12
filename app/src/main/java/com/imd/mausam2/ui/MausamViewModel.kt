package com.imd.mausam2.ui

import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imd.mausam2.data.model.CompleteWeatherData
import com.imd.mausam2.data.model.LocationInfo
import com.imd.mausam2.data.model.PersonaType
import com.imd.mausam2.data.repository.WeatherRepository
import com.imd.mausam2.domain.SmartGlanceAI
import com.imd.mausam2.ui.components.NavTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class MausamViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val repository = WeatherRepository(application)
    private val prefs = application.getSharedPreferences("mausam2_user_prefs", Application.MODE_PRIVATE)

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

    private val _isPlayingAudio = MutableStateFlow(false)
    val isPlayingAudio: StateFlow<Boolean> = _isPlayingAudio.asStateFlow()

    // Tab Navigation State
    private val _currentTab = MutableStateFlow(NavTab.HOME)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    // Onboarding Dialog State (True if never completed or if user requests re-customizing)
    private val _showOnboarding = MutableStateFlow(!prefs.getBoolean("has_completed_onboarding", false))
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    private var tts: TextToSpeech? = null
    private var ttsReady = false

    init {
        tts = TextToSpeech(application, this)
        loadWeather(WeatherRepository.PRESET_CITIES.first())
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            ttsReady = true
            updateTtsLanguage(_isHindi.value)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isPlayingAudio.value = true
                }
                override fun onDone(utteranceId: String?) {
                    _isPlayingAudio.value = false
                }
                override fun onError(utteranceId: String?) {
                    _isPlayingAudio.value = false
                }
            })
        }
    }

    private fun updateTtsLanguage(hindi: Boolean) {
        if (!ttsReady) return
        val loc = if (hindi) Locale("hi", "IN") else Locale.US
        val res = tts?.setLanguage(loc)
        if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.language = Locale.US
        }
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
        updateTtsLanguage(newLang)
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
        if (_isPlayingAudio.value) {
            tts?.stop()
            _isPlayingAudio.value = false
        } else {
            val text = _smartGlanceText.value
            if (text.isNotBlank() && ttsReady) {
                _isPlayingAudio.value = true
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "mausam_speech_id")
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
        tts?.stop()
        tts?.shutdown()
    }
}
