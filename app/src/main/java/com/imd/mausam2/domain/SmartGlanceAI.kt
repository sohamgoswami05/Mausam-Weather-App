package com.imd.mausam2.domain

import com.imd.mausam2.data.model.CompleteWeatherData
import com.imd.mausam2.data.model.PersonaType

object SmartGlanceAI {

    fun generateInsightEn(data: CompleteWeatherData, activePersonas: Set<PersonaType>): String {
        val snippets = mutableListOf<String>()

        // Base greeting
        snippets.add("Today in ${data.location.cityName}: ${data.currentTemp.toInt()}°C with ${data.conditionText.lowercase()}.")

        // Persona specific intelligence
        if (activePersonas.contains(PersonaType.HEALTH)) {
            val aqiNote = when {
                data.aqi > 300 -> "AQI is Hazardous (${data.aqi}); N95 mask required."
                data.aqi > 200 -> "AQI is Poor (${data.aqi}); limit outdoor exertion."
                data.aqi > 100 -> "AQI is Moderate (${data.aqi})."
                else -> "Air quality is Clean & Pure (AQI ${data.aqi})."
            }
            snippets.add(aqiNote)
        }

        if (activePersonas.contains(PersonaType.FITNESS)) {
            snippets.add("Workout suitability score is ${data.runScore}/100. Best running window is ${data.bestRunningWindow}.")
        }

        if (activePersonas.contains(PersonaType.BEACH)) {
            snippets.add("Coastal waters are rated '${data.seaCondition}' with ${data.waveHeight}m swells.")
        }

        if (activePersonas.contains(PersonaType.AGRICULTURE)) {
            snippets.add(if (data.pesticideSpraySafe) "Agromet: Safe window for fertilizer spray today." else "Agromet: Postpone spray (rain/wind risk).")
        }

        if (activePersonas.contains(PersonaType.COMMUTE) || activePersonas.contains(PersonaType.PARENTS)) {
            if (data.visibilityMeters < 300) {
                snippets.add("Dense fog alert for morning school & office transit (${data.visibilityMeters}m visibility).")
            } else if (data.imdAlert.level != com.imd.mausam2.data.model.IMDWarningLevel.GREEN) {
                snippets.add("Commuters note: ${data.imdAlert.headline}.")
            }
        }

        if (snippets.size == 1) {
            snippets.add(data.imdAlert.advice)
        }

        return snippets.joinToString(" ")
    }

    fun generateInsightHi(data: CompleteWeatherData, activePersonas: Set<PersonaType>): String {
        val snippets = mutableListOf<String>()

        snippets.add("${data.location.cityName} में आज: ${data.currentTemp.toInt()}°C और ${data.conditionTextHi}।")

        if (activePersonas.contains(PersonaType.HEALTH)) {
            val aqiNote = when {
                data.aqi > 300 -> "वायु गुणवत्ता अत्यंत गंभीर है (AQI ${data.aqi}), N95 मास्क अवश्य पहनें।"
                data.aqi > 200 -> "वायु गुणवत्ता खराब है (AQI ${data.aqi}), बाहर अधिक समय न बिताएं।"
                else -> "वायु गुणवत्ता संतोषजनक है (AQI ${data.aqi})।"
            }
            snippets.add(aqiNote)
        }

        if (activePersonas.contains(PersonaType.FITNESS)) {
            snippets.add("दौड़ के लिए स्कोर ${data.runScore}/100 है। उत्तम समय ${data.bestRunningWindow} रहेगा।")
        }

        if (activePersonas.contains(PersonaType.BEACH)) {
            snippets.add("समुद्र की स्थिति: '${data.seaConditionHi}' एवं लहरें ${data.waveHeight} मीटर।")
        }

        if (activePersonas.contains(PersonaType.AGRICULTURE)) {
            snippets.add(if (data.pesticideSpraySafe) "कृषि सलाह: आज कीटनाशक छिड़काव के लिए अनुकूल मौसम।" else "कृषि सलाह: बारिश की संभावना, छिड़काव स्थगित करें।")
        }

        if (activePersonas.contains(PersonaType.COMMUTE) || activePersonas.contains(PersonaType.PARENTS)) {
            if (data.visibilityMeters < 300) {
                snippets.add("स्कूल एवं ऑफिस यात्रा के दौरान कोहरे से सावधान रहें (दृश्यता ${data.visibilityMeters} मी)।")
            }
        }

        if (snippets.size == 1) {
            snippets.add(data.imdAlert.adviceHi)
        }

        return snippets.joinToString(" ")
    }
}
