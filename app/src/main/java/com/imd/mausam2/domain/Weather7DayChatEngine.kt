package com.imd.mausam2.domain

import com.imd.mausam2.data.model.CompleteWeatherData
import com.imd.mausam2.data.model.DayForecast
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val dayHighlights: List<DayHighlight> = emptyList()
)

data class DayHighlight(
    val dayName: String,
    val condition: String,
    val tempRange: String,
    val rainChance: Int
)

object Weather7DayChatEngine {

    fun generateInitialGreeting(cityName: String, isHindi: Boolean): ChatMessage {
        val greetingText = if (isHindi) {
            "नमस्ते! मैं $cityName के लिए मौसम 7-दिवसीय AI सहायक हूँ।⚡\n\nआप मुझसे आगामी 7 दिनों के मौसम (जैसे बारिश, तापमान, सप्ताहांत यात्रा, आंधी-तूफान, या आउटडोर योजना) के बारे में कोई भी प्रश्न पूछ सकते हैं।"
        } else {
            "Hello! I am your Mausam 7-Day AI Weather Assistant for $cityName.⚡\n\nAsk me anything about the upcoming 7-day forecast (rain probability, hottest/coolest days, weekend plans, storms, or outdoor activity suitability)!"
        }
        return ChatMessage(text = greetingText, isUser = false)
    }

    fun answerQuery(
        userQuery: String,
        data: CompleteWeatherData,
        isHindi: Boolean
    ): ChatMessage {
        val query = userQuery.trim().lowercase()
        val days = data.sevenDayForecast
        val cityName = data.location.cityName

        // 1. Check for specific day of week query
        val targetDay = findMentionedDay(query, days)
        if (targetDay != null) {
            return buildSpecificDayAnswer(targetDay, cityName, isHindi)
        }

        // 2. Rain / Precipitation queries
        if (query.contains("rain") || query.contains("बारिश") || query.contains("वर्षा") || query.contains("umbrella") || query.contains("छाता") || query.contains("shower")) {
            return buildRainAnswer(days, cityName, isHindi)
        }

        // 3. Weekend queries (Saturday / Sunday)
        if (query.contains("weekend") || query.contains("शनिवार") || query.contains("रविवार") || query.contains("sat") || query.contains("sun") || query.contains("छुट्टी") || query.contains("trip")) {
            return buildWeekendAnswer(days, cityName, isHindi)
        }

        // 4. Hottest / Maximum temperature queries
        if (query.contains("hot") || query.contains("गरम") || query.contains("गर्म") || query.contains("highest") || query.contains("maximum") || query.contains("max temp") || query.contains("heat")) {
            return buildHottestDayAnswer(days, cityName, isHindi)
        }

        // 5. Coldest / Minimum temperature queries
        if (query.contains("cold") || query.contains("ठंडा") || query.contains("ठंड") || query.contains("cool") || query.contains("minimum") || query.contains("min temp")) {
            return buildCoolestDayAnswer(days, cityName, isHindi)
        }

        // 6. Storm / Thunderstorm / Wind queries
        if (query.contains("storm") || query.contains("thunder") || query.contains("आंधी") || query.contains("तूफान") || query.contains("बिजली") || query.contains("lightning") || query.contains("wind") || query.contains("हवा")) {
            return buildStormAnswer(days, data, cityName, isHindi)
        }

        // 7. Outdoor / Workout / Sports suitability
        if (query.contains("run") || query.contains("jog") || query.contains("workout") || query.contains("दौड़") || query.contains("outdoor") || query.contains("sports") || query.contains("खेल")) {
            return buildWorkoutWeekAnswer(days, cityName, isHindi)
        }

        // 8. Tomorrow's forecast specifically
        if (query.contains("tomorrow") || query.contains("कल")) {
            val tomorrow = days.getOrNull(1) ?: days.firstOrNull()
            if (tomorrow != null) {
                return buildSpecificDayAnswer(tomorrow, cityName, isHindi)
            }
        }

        // 9. Default: Full 7-Day Comprehensive Summary
        return buildFullWeekSummary(days, cityName, isHindi)
    }

    private fun findMentionedDay(query: String, days: List<DayForecast>): DayForecast? {
        val dayNamesEn = listOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")
        val dayNamesHi = listOf("सोमवार", "मंगलवार", "बुधवार", "गुरुवार", "शुक्रवार", "शनिवार", "रविवार")

        // Check each day in 7-day forecast
        for (day in days) {
            val name = day.dayOfWeek.lowercase()
            if (query.contains(name)) return day
            if (query.contains("today") && name == "today") return day
            if (query.contains("आज") && name == "today") return day
        }

        for (i in dayNamesEn.indices) {
            if (query.contains(dayNamesEn[i]) || query.contains(dayNamesHi[i])) {
                val matched = days.find {
                    it.dayOfWeek.lowercase().contains(dayNamesEn[i].take(3))
                }
                if (matched != null) return matched
            }
        }

        return null
    }

    private fun buildSpecificDayAnswer(day: DayForecast, cityName: String, isHindi: Boolean): ChatMessage {
        val highlights = listOf(
            DayHighlight(
                dayName = day.dayOfWeek,
                condition = if (isHindi) day.conditionTextHi else day.conditionText,
                tempRange = "${day.minTemp.toInt()}°C - ${day.maxTemp.toInt()}°C",
                rainChance = day.rainProbability
            )
        )

        val text = if (isHindi) {
            """
            📅 **${day.dayOfWeek} (${day.date}) का मौसम - $cityName:**
            • स्थिति: ${day.conditionTextHi}
            • अधिकतम तापमान: ${day.maxTemp.toInt()}°C | न्यूनतम: ${day.minTemp.toInt()}°C
            • वर्षा की संभावना: ${day.rainProbability}%
            • अधिकतम हवा की गति: ${day.windSpeedMax.toInt()} किमी/घंटा
            • यूवी इंडेक्स: ${day.uvIndexMax.toInt()}
            
            💡 **सलाह:** ${if (day.rainProbability > 40) "बारिश की संभावना है, छाता साथ रखें!" else "मौसम सामान्य एवं अनुकूल रहेगा।"}
            """.trimIndent()
        } else {
            """
            📅 **${day.dayOfWeek} (${day.date}) Forecast for $cityName:**
            • Condition: ${day.conditionText}
            • High: ${day.maxTemp.toInt()}°C | Low: ${day.minTemp.toInt()}°C
            • Rain Probability: ${day.rainProbability}%
            • Peak Wind: ${day.windSpeedMax.toInt()} km/h
            • UV Index: ${day.uvIndexMax.toInt()}
            
            💡 **Advice:** ${if (day.rainProbability > 40) "Rain expected; carry an umbrella and anticipate damp roads!" else "Fair weather expected; great day for routine outdoor plans."}
            """.trimIndent()
        }

        return ChatMessage(text = text, isUser = false, dayHighlights = highlights)
    }

    private fun buildRainAnswer(days: List<DayForecast>, cityName: String, isHindi: Boolean): ChatMessage {
        val rainDays = days.filter { it.rainProbability >= 30 || it.weatherCode in listOf(51, 53, 55, 61, 63, 65, 80, 81, 82, 95, 96, 99) }
        val highlights = days.map {
            DayHighlight(
                dayName = it.dayOfWeek,
                condition = if (isHindi) it.conditionTextHi else it.conditionText,
                tempRange = "${it.maxTemp.toInt()}°C",
                rainChance = it.rainProbability
            )
        }

        val text = if (isHindi) {
            if (rainDays.isNotEmpty()) {
                val dayListStr = rainDays.joinToString("\n") { "• **${it.dayOfWeek}**: ${it.rainProbability}% वर्षा संभावना (${it.conditionTextHi})" }
                """
                🌧️ **$cityName में आगामी 7 दिनों में वर्षा का पूर्वानुमान:**
                
                निम्नलिखित दिनों में बारिश की संभावना है:
                $dayListStr
                
                💡 **सलाह:** इन दिनों यात्रा या आउटडोर गतिविधियों के दौरान रेनकोट या छाता अवश्य साथ रखें।
                """.trimIndent()
            } else {
                "☀️ **$cityName में आगामी 7 दिनों में किसी महत्वपूर्ण बारिश की संभावना नहीं है।**\n\nसप्ताह भर मुख्य रूप से शुष्क और साफ मौसम रहने का अनुमान है।"
            }
        } else {
            if (rainDays.isNotEmpty()) {
                val dayListStr = rainDays.joinToString("\n") { "• **${it.dayOfWeek}**: ${it.rainProbability}% rain chance (${it.conditionText})" }
                """
                🌧️ **7-Day Rain Outlook for $cityName:**
                
                Precipitation is expected on the following days:
                $dayListStr
                
                💡 **Advice:** Plan wet-weather alternatives and keep waterproof gear handy on these days.
                """.trimIndent()
            } else {
                "☀️ **No significant rain is predicted for $cityName over the next 7 days.**\n\nThe week will remain predominantly dry with clear to partly cloudy skies."
            }
        }

        return ChatMessage(text = text, isUser = false, dayHighlights = highlights.take(4))
    }

    private fun buildWeekendAnswer(days: List<DayForecast>, cityName: String, isHindi: Boolean): ChatMessage {
        val weekendDays = days.filter {
            val name = it.dayOfWeek.lowercase()
            name.contains("sat") || name.contains("sun")
        }

        val targetWeekend = if (weekendDays.isNotEmpty()) weekendDays else days.takeLast(2)

        val highlights = targetWeekend.map {
            DayHighlight(
                dayName = it.dayOfWeek,
                condition = if (isHindi) it.conditionTextHi else it.conditionText,
                tempRange = "${it.minTemp.toInt()}°C - ${it.maxTemp.toInt()}°C",
                rainChance = it.rainProbability
            )
        }

        val text = if (isHindi) {
            val details = targetWeekend.joinToString("\n") {
                "• **${it.dayOfWeek}**: ${it.conditionTextHi}, अधिकतम ${it.maxTemp.toInt()}°C, बारिश ${it.rainProbability}%"
            }
            """
            🏕️ **$cityName के लिए सप्ताहांत (Weekend) मौसम पूर्वानुमान:**
            $details
            
            💡 **सप्ताहांत सारांश:** ${if (targetWeekend.any { it.rainProbability > 40 }) "सप्ताहांत में कुछ समय बारिश हो सकती है, इनडोर अथवा सावधानीपूर्वक आउटडोर योजना बनाएं।" else "सप्ताहांत आउटडोर यात्रा, पिकनिक और खेलकूद के लिए बहुत अनुकूल रहेगा!"}
            """.trimIndent()
        } else {
            val details = targetWeekend.joinToString("\n") {
                "• **${it.dayOfWeek}**: ${it.conditionText}, High ${it.maxTemp.toInt()}°C, Rain ${it.rainProbability}%"
            }
            """
            🏕️ **Weekend Weather Outlook for $cityName:**
            $details
            
            💡 **Weekend Verdict:** ${if (targetWeekend.any { it.rainProbability > 40 }) "Rain showers may occur on the weekend; check radar before heading out." else "Great conditions for outdoor events, weekend road trips, and fitness!"}
            """.trimIndent()
        }

        return ChatMessage(text = text, isUser = false, dayHighlights = highlights)
    }

    private fun buildHottestDayAnswer(days: List<DayForecast>, cityName: String, isHindi: Boolean): ChatMessage {
        val hottest = days.maxByOrNull { it.maxTemp } ?: days.first()
        val text = if (isHindi) {
            """
            🔥 **सप्ताह का सबसे गर्म दिन: ${hottest.dayOfWeek} (${hottest.date})**
            • उच्चतम तापमान: **${hottest.maxTemp.toInt()}°C**
            • न्यूनतम तापमान: ${hottest.minTemp.toInt()}°C
            • मौसम स्थिति: ${hottest.conditionTextHi}
            • यूवी इंडेक्स: ${hottest.uvIndexMax.toInt()}
            
            💡 दोपहर 12 बजे से 3 बजे के बीच पर्याप्त पानी पिएं और सीधी धूप से बचें।
            """.trimIndent()
        } else {
            """
            🔥 **Hottest Day of the Week: ${hottest.dayOfWeek} (${hottest.date})**
            • Peak Temperature: **${hottest.maxTemp.toInt()}°C**
            • Overnight Low: ${hottest.minTemp.toInt()}°C
            • Condition: ${hottest.conditionText}
            • UV Index: ${hottest.uvIndexMax.toInt()}
            
            💡 Stay hydrated and minimize direct solar exposure during peak afternoon hours (12–3 PM).
            """.trimIndent()
        }

        return ChatMessage(
            text = text,
            isUser = false,
            dayHighlights = listOf(
                DayHighlight(hottest.dayOfWeek, if (isHindi) hottest.conditionTextHi else hottest.conditionText, "${hottest.maxTemp.toInt()}°C Max", hottest.rainProbability)
            )
        )
    }

    private fun buildCoolestDayAnswer(days: List<DayForecast>, cityName: String, isHindi: Boolean): ChatMessage {
        val coolest = days.minByOrNull { it.minTemp } ?: days.first()
        val text = if (isHindi) {
            """
            ❄️ **सप्ताह का सबसे ठंडा दिन/रात: ${coolest.dayOfWeek} (${coolest.date})**
            • न्यूनतम तापमान: **${coolest.minTemp.toInt()}°C**
            • अधिकतम तापमान: ${coolest.maxTemp.toInt()}°C
            • मौसम स्थिति: ${coolest.conditionTextHi}
            
            💡 सुबह व देर रात हल्की ठंड रह सकती है।
            """.trimIndent()
        } else {
            """
            ❄️ **Coolest Day/Night of the Week: ${coolest.dayOfWeek} (${coolest.date})**
            • Minimum Overnight Low: **${coolest.minTemp.toInt()}°C**
            • Daytime High: ${coolest.maxTemp.toInt()}°C
            • Condition: ${coolest.conditionText}
            
            💡 Expect a crisp morning and pleasant evening breeze.
            """.trimIndent()
        }

        return ChatMessage(
            text = text,
            isUser = false,
            dayHighlights = listOf(
                DayHighlight(coolest.dayOfWeek, if (isHindi) coolest.conditionTextHi else coolest.conditionText, "${coolest.minTemp.toInt()}°C Min", coolest.rainProbability)
            )
        )
    }

    private fun buildStormAnswer(days: List<DayForecast>, data: CompleteWeatherData, cityName: String, isHindi: Boolean): ChatMessage {
        val stormDays = days.filter { it.weatherCode in listOf(95, 96, 99) || it.windSpeedMax >= 28.0 }

        val text = if (isHindi) {
            if (stormDays.isNotEmpty()) {
                val listStr = stormDays.joinToString("\n") { "• **${it.dayOfWeek}**: ${it.conditionTextHi}, अधिकतम हवा ${it.windSpeedMax.toInt()} किमी/घंटा" }
                """
                ⚡ **आंधी-तूफान एवं तेज हवा अलर्ट - $cityName:**
                $listStr
                
                • आईएमडी चेतावनी स्तर: ${data.imdAlert.level.labelHi}
                • आपातकालीन नंबर: 112 / 1070
                
                💡 वज्रपात के समय पेड़ों के नीचे शरण न लें और खुले मैदानों से दूर रहें।
                """.trimIndent()
            } else {
                """
                🛡️ **$cityName में आगामी 7 दिनों में किसी गंभीर आंधी या तूफान की संभावना नहीं है।**
                
                • अधिकतम हवा की गति सामान्य स्तर (${days.maxOfOrNull { it.windSpeedMax }?.toInt() ?: 15} किमी/घंटा) पर रहेगी।
                • आईएमडी चेतावनी स्तर: ${data.imdAlert.level.labelHi}
                """.trimIndent()
            }
        } else {
            if (stormDays.isNotEmpty()) {
                val listStr = stormDays.joinToString("\n") { "• **${it.dayOfWeek}**: ${it.conditionText}, Peak Wind ${it.windSpeedMax.toInt()} km/h" }
                """
                ⚡ **Storm & High Wind Alert for $cityName:**
                $listStr
                
                • IMD Warning Level: ${data.imdAlert.level.labelEn}
                • Disaster Helpline: 112 / 1070
                
                💡 Seek shelter indoors during thunder bursts; avoid open metallic structures and tall isolated trees.
                """.trimIndent()
            } else {
                """
                🛡️ **No severe storms or squalls predicted for $cityName over the next 7 days.**
                
                • Peak wind speeds will remain moderate (up to ${days.maxOfOrNull { it.windSpeedMax }?.toInt() ?: 15} km/h).
                • Current IMD Alert Level: ${data.imdAlert.level.labelEn}.
                """.trimIndent()
            }
        }

        return ChatMessage(text = text, isUser = false)
    }

    private fun buildWorkoutWeekAnswer(days: List<DayForecast>, cityName: String, isHindi: Boolean): ChatMessage {
        val bestDays = days.filter { it.rainProbability <= 25 && it.maxTemp <= 34.0 }
        val targetBest = if (bestDays.isNotEmpty()) bestDays else days.take(3)

        val text = if (isHindi) {
            val listStr = targetBest.joinToString("\n") { "• **${it.dayOfWeek}**: ${it.conditionTextHi}, ${it.minTemp.toInt()}°C–${it.maxTemp.toInt()}°C (बारिश ${it.rainProbability}%)" }
            """
            🏃 **दौड़ एवं आउटडोर फिटनेस के लिए सर्वश्रेष्ठ दिन - $cityName:**
            $listStr
            
            💡 सुबह 6:00 से 8:00 बजे का समय सबसे सुखद रहेगा जब तापमान न्यूनतम और हवा शुद्ध रहती है।
            """.trimIndent()
        } else {
            val listStr = targetBest.joinToString("\n") { "• **${it.dayOfWeek}**: ${it.conditionText}, ${it.minTemp.toInt()}°C–${it.maxTemp.toInt()}°C (Rain ${it.rainProbability}%)" }
            """
            🏃 **Best Days for Running & Outdoor Fitness in $cityName:**
            $listStr
            
            💡 Optimal running window: 06:00 AM – 08:00 AM when ambient temperatures are at daily minimums and air is crisp.
            """.trimIndent()
        }

        return ChatMessage(
            text = text,
            isUser = false,
            dayHighlights = targetBest.take(3).map {
                DayHighlight(it.dayOfWeek, if (isHindi) it.conditionTextHi else it.conditionText, "${it.maxTemp.toInt()}°C", it.rainProbability)
            }
        )
    }

    private fun buildFullWeekSummary(days: List<DayForecast>, cityName: String, isHindi: Boolean): ChatMessage {
        val highTemp = days.maxOfOrNull { it.maxTemp }?.toInt() ?: 32
        val lowTemp = days.minOfOrNull { it.minTemp }?.toInt() ?: 22
        val rainyDays = days.filter { it.rainProbability > 35 }.map { it.dayOfWeek }

        val highlights = days.take(5).map {
            DayHighlight(
                dayName = it.dayOfWeek,
                condition = if (isHindi) it.conditionTextHi else it.conditionText,
                tempRange = "${it.minTemp.toInt()}°C - ${it.maxTemp.toInt()}°C",
                rainChance = it.rainProbability
            )
        }

        val text = if (isHindi) {
            """
            📊 **$cityName का 7-दिवसीय मौसम सारांश:**
            • तापमान सीमा: ${lowTemp}°C से ${highTemp}°C
            • बारिश की स्थिति: ${if (rainyDays.isNotEmpty()) "${rainyDays.joinToString(", ")} को बारिश के आसार" else "पूरा सप्ताह मुख्यतः शुष्क रहेगा"}
            
            आप मुझसे किसी विशिष्ट दिन (जैसे 'कल', 'रविवार'), तापमान या आउटडोर योजना के बारे में पूछ सकते हैं!
            """.trimIndent()
        } else {
            """
            📊 **7-Day Weather Summary for $cityName:**
            • Temperature Range: ${lowTemp}°C to ${highTemp}°C across the week
            • Precipitation: ${if (rainyDays.isNotEmpty()) "Showers likely on ${rainyDays.joinToString(", ")}" else "Generally dry conditions prevailing throughout the week"}
            
            Feel free to ask about any specific day (e.g., 'What about Sunday?', 'Will it rain on Thursday?') or activities!
            """.trimIndent()
        }

        return ChatMessage(text = text, isUser = false, dayHighlights = highlights)
    }
}
