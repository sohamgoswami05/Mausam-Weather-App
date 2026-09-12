package com.imd.mausam2.data.model

import com.google.gson.annotations.SerializedName

// User Personas matching the SIH problem statement
enum class PersonaType(val id: String, val titleEn: String, val titleHi: String, val iconName: String) {
    HEALTH("health", "Health & AQI", "स्वास्थ्य एवं AQI", "HeartPulse"),
    FITNESS("fitness", "Outdoor Fitness", "फिटनेस एवं दौड़", "DirectionsRun"),
    BEACH("beach", "Beach & Coastal", "तटीय एवं समुद्र", "Waves"),
    AGRICULTURE("agriculture", "Kisan & Agri", "किसान एवं कृषि", "Agriculture"),
    COMMUTE("commute", "Daily Commute", "दैनिक यात्रा", "Commute"),
    PARENTS("parents", "School & Family", "स्कूल एवं परिवार", "FamilyRestroom"),
    TRAVEL("travel", "Travel & Events", "यात्रा एवं आयोजन", "FlightTakeoff"),
    SEVERE("severe", "Storm & Lightning", "आंधी एवं बिजली", "ElectricBolt")
}

// IMD 4-Stage Warning Color Schema
enum class IMDWarningLevel(val colorHex: Long, val labelEn: String, val labelHi: String) {
    GREEN(0xFF4CAF50, "Green - No Warning", "हरा - कोई चेतावनी नहीं"),
    YELLOW(0xFFFFC107, "Yellow - Be Updated", "पीला - सतर्क रहें"),
    ORANGE(0xFFFF9800, "Orange - Be Prepared", "नारंगी - तैयार रहें"),
    RED(0xFFE53935, "Red - Take Action", "लाल - त्वरित कार्रवाई करें")
}

data class IMDAlert(
    val level: IMDWarningLevel,
    val headline: String,
    val headlineHi: String,
    val description: String,
    val descriptionHi: String,
    val advice: String,
    val adviceHi: String
)

data class LocationInfo(
    val cityName: String,
    val stateName: String,
    val latitude: Double,
    val longitude: Double,
    val isGps: Boolean = false
)

// Processed single day forecast
data class DayForecast(
    val date: String,
    val dayOfWeek: String,
    val weatherCode: Int,
    val conditionText: String,
    val conditionTextHi: String,
    val maxTemp: Double,
    val minTemp: Double,
    val rainProbability: Int,
    val uvIndexMax: Double,
    val windSpeedMax: Double,
    val hourlyList: List<HourForecast> = emptyList()
)

data class HourForecast(
    val timeStr: String,
    val temp: Double,
    val weatherCode: Int,
    val rainChance: Int,
    val humidity: Int,
    val windSpeed: Double
)

// Main aggregate data model
data class CompleteWeatherData(
    val location: LocationInfo,
    val currentTemp: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherCode: Int,
    val conditionText: String,
    val conditionTextHi: String,
    val uvIndex: Double,
    val aqi: Int,
    val pm25: Double,
    val pm10: Double,
    val soilMoisture: Double, // 0-100%
    val waveHeight: Double, // meters
    val seaCondition: String, // Safe, Caution, Rough
    val seaConditionHi: String,
    val visibilityMeters: Int,
    val runScore: Int, // 0-100
    val bestRunningWindow: String,
    val schoolCommuteRating: String, // Good, Caution, Risky
    val pesticideSpraySafe: Boolean,
    val sprayAdvice: String,
    val eventComfortIndex: Int, // 0-100
    val packingAdvice: String,
    val packingAdviceHi: String,
    val lightningProximityKm: Int?, // Concept integration with Damini
    val imdAlert: IMDAlert,
    val sevenDayForecast: List<DayForecast>,
    val cachedTimestamp: Long = System.currentTimeMillis(),
    val isFromCache: Boolean = false
)

// Open-Meteo raw API models
data class OpenMeteoForecastResponse(
    @SerializedName("current") val current: CurrentUnits?,
    @SerializedName("hourly") val hourly: HourlyUnits?,
    @SerializedName("daily") val daily: DailyUnits?
)

data class CurrentUnits(
    @SerializedName("temperature_2m") val temperature2m: Double,
    @SerializedName("relative_humidity_2m") val relativeHumidity2m: Int,
    @SerializedName("apparent_temperature") val apparentTemperature: Double,
    @SerializedName("precipitation") val precipitation: Double,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("wind_speed_10m") val windSpeed10m: Double,
    @SerializedName("uv_index") val uvIndex: Double?
)

data class HourlyUnits(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m") val temperature2m: List<Double>,
    @SerializedName("relative_humidity_2m") val relativeHumidity2m: List<Int>,
    @SerializedName("precipitation_probability") val precipitationProbability: List<Int>?,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("visibility") val visibility: List<Double>?,
    @SerializedName("wind_speed_10m") val windSpeed10m: List<Double>
)

data class DailyUnits(
    @SerializedName("time") val time: List<String>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max") val temperature2mMax: List<Double>,
    @SerializedName("temperature_2m_min") val temperature2mMin: List<Double>,
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: List<Int>?,
    @SerializedName("uv_index_max") val uvIndexMax: List<Double>?,
    @SerializedName("wind_speed_10m_max") val windSpeed10mMax: List<Double>?
)

data class OpenMeteoAirQualityResponse(
    @SerializedName("current") val currentAqi: CurrentAqiUnits?
)

data class CurrentAqiUnits(
    @SerializedName("pm2_5") val pm25: Double?,
    @SerializedName("pm10") val pm10: Double?,
    @SerializedName("us_aqi") val usAqi: Int?
)
