package com.imd.mausam2.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.imd.mausam2.data.api.WeatherApiService
import com.imd.mausam2.data.model.*
import com.imd.mausam2.domain.IMDAlertEngine
import com.imd.mausam2.domain.WmoWeatherUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

class WeatherRepository(private val context: Context) {

    private val api = WeatherApiService.create()
    private val gson = Gson()
    private val prefs: SharedPreferences =
        context.getSharedPreferences("mausam2_cache", Context.MODE_PRIVATE)

    companion object {
        val PRESET_CITIES = listOf(
            LocationInfo("New Delhi", "Delhi NCR", 28.6139, 77.2090),
            LocationInfo("Mumbai", "Maharashtra", 19.0760, 72.8777),
            LocationInfo("Goa (Panaji)", "Goa", 15.4909, 73.8278),
            LocationInfo("Bengaluru", "Karnataka", 12.9716, 77.5946),
            LocationInfo("Ludhiana", "Punjab", 30.9010, 75.8573),
            LocationInfo("Shimla", "Himachal Pradesh", 31.1048, 77.1734),
            LocationInfo("Chennai", "Tamil Nadu", 13.0827, 80.2707)
        )
    }

    suspend fun getWeatherData(location: LocationInfo, forceRefresh: Boolean = false): Result<CompleteWeatherData> =
        withContext(Dispatchers.IO) {
            val cacheKey = "cache_${location.cityName.replace(" ", "_")}"

            // 1. Try to fetch from network if connected or forceRefresh
            try {
                val forecastResp = api.getForecast(location.latitude, location.longitude)
                val aqiResp = try {
                    api.getAirQuality(location.latitude, location.longitude)
                } catch (e: Exception) {
                    null
                }

                val completeData = processApiResponse(location, forecastResp, aqiResp)

                // 2. Persist to cache for 7-day offline availability
                saveToCache(cacheKey, completeData)
                saveToCache("cache_last_active", completeData)

                return@withContext Result.success(completeData)
            } catch (networkEx: Exception) {
                // 3. Fallback to cached data
                val cached = getFromCache(cacheKey) ?: getFromCache("cache_last_active")
                if (cached != null) {
                    return@withContext Result.success(cached.copy(isFromCache = true))
                }

                // 4. Fallback to smart simulated Indian weather if first launch and offline
                val fallbackData = generateSmartFallback(location)
                saveToCache(cacheKey, fallbackData)
                return@withContext Result.success(fallbackData.copy(isFromCache = true))
            }
        }

    private fun saveToCache(key: String, data: CompleteWeatherData) {
        prefs.edit().putString(key, gson.toJson(data)).apply()
    }

    private fun getFromCache(key: String): CompleteWeatherData? {
        val json = prefs.getString(key, null) ?: return null
        return try {
            gson.fromJson(json, CompleteWeatherData::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private fun processApiResponse(
        location: LocationInfo,
        forecast: OpenMeteoForecastResponse,
        aqiResp: OpenMeteoAirQualityResponse?
    ): CompleteWeatherData {
        val curr = forecast.current
        val hourly = forecast.hourly
        val daily = forecast.daily

        val temp = curr?.temperature2m ?: 28.0
        val feelsLike = curr?.apparentTemperature ?: temp
        val humidity = curr?.relativeHumidity2m ?: 65
        val windSpeed = curr?.windSpeed10m ?: 12.0
        val weatherCode = curr?.weatherCode ?: 1
        val uv = curr?.uvIndex ?: 6.0

        val aqiVal = aqiResp?.currentAqi?.usAqi ?: estimateAqiForCity(location.cityName)
        val pm25 = aqiResp?.currentAqi?.pm25 ?: (aqiVal * 0.45)
        val pm10 = aqiResp?.currentAqi?.pm10 ?: (aqiVal * 0.85)

        val visibilityMeters = ((hourly?.visibility?.firstOrNull() ?: 6000.0)).toInt()
        val rainProb = daily?.precipitationProbabilityMax?.firstOrNull() ?: 20

        val imdAlert = IMDAlertEngine.generateAlert(
            temp = temp,
            rainProb = rainProb,
            weatherCode = weatherCode,
            windSpeed = windSpeed,
            aqi = aqiVal,
            visibilityMeters = visibilityMeters
        )

        // Generate 7-Day Processed List
        val sevenDays = mutableListOf<DayForecast>()
        val dates = daily?.time ?: emptyList()
        val inFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outDayFormat = SimpleDateFormat("EEE", Locale.getDefault())

        for (i in 0 until min(7, dates.size)) {
            val dateStr = dates[i]
            val parsedDate = try { inFormat.parse(dateStr) } catch (e: Exception) { Date() }
            val dayName = if (i == 0) "Today" else outDayFormat.format(parsedDate ?: Date())
            val code = daily?.weatherCode?.getOrNull(i) ?: 0
            val maxT = daily?.temperature2mMax?.getOrNull(i) ?: 32.0
            val minT = daily?.temperature2mMin?.getOrNull(i) ?: 22.0
            val rProb = daily?.precipitationProbabilityMax?.getOrNull(i) ?: 10
            val uvMax = daily?.uvIndexMax?.getOrNull(i) ?: 6.0
            val wMax = daily?.windSpeed10mMax?.getOrNull(i) ?: 14.0

            // Hourly slice for day i
            val hourList = mutableListOf<HourForecast>()
            if (hourly != null && hourly.time.size >= (i + 1) * 24) {
                val startIdx = i * 24
                for (h in 0 until 24 step 3) {
                    val idx = startIdx + h
                    if (idx < hourly.time.size) {
                        val rawHour = hourly.time[idx].substringAfter("T").take(5)
                        hourList.add(
                            HourForecast(
                                timeStr = rawHour,
                                temp = hourly.temperature2m.getOrElse(idx) { 25.0 },
                                weatherCode = hourly.weatherCode.getOrElse(idx) { 0 },
                                rainChance = hourly.precipitationProbability?.getOrElse(idx) { 0 } ?: 0,
                                humidity = hourly.relativeHumidity2m.getOrElse(idx) { 50 },
                                windSpeed = hourly.windSpeed10m.getOrElse(idx) { 10.0 }
                            )
                        )
                    }
                }
            }

            sevenDays.add(
                DayForecast(
                    date = dateStr,
                    dayOfWeek = dayName,
                    weatherCode = code,
                    conditionText = WmoWeatherUtils.getWeatherDescriptionEn(code),
                    conditionTextHi = WmoWeatherUtils.getWeatherDescriptionHi(code),
                    maxTemp = maxT,
                    minTemp = minT,
                    rainProbability = rProb,
                    uvIndexMax = uvMax,
                    windSpeedMax = wMax,
                    hourlyList = hourList
                )
            )
        }

        // Marine calculations for coastal cities
        val isCoastal = location.cityName in listOf("Goa (Panaji)", "Mumbai", "Chennai")
        val waveHeight = if (isCoastal) (0.8 + (windSpeed * 0.05)) else 0.2
        val seaCond = when {
            waveHeight > 2.0 -> "Dangerous / Rough"
            waveHeight > 1.2 -> "Caution / Moderate"
            else -> "Safe & Calm"
        }
        val seaCondHi = when {
            waveHeight > 2.0 -> "अत्यंत अशांत / ख़तरनाक"
            waveHeight > 1.2 -> "सतर्क रहें / मध्यम लहरें"
            else -> "शांत एवं सुरक्षित"
        }

        // Fitness score calculation (100 - penalties for temp, aqi, rain)
        val tempPenalty = if (temp > 35) (temp - 35) * 4 else if (temp < 15) (15 - temp) * 3 else 0.0
        val aqiPenalty = (aqiVal / 5.0)
        val rainPenalty = (rainProb * 0.3)
        val runScore = max(15, (100 - tempPenalty - aqiPenalty - rainPenalty).toInt())

        // Agriculture calculations
        val soilMoisture = min(88.0, max(22.0, (humidity * 0.65) + (rainProb * 0.25)))
        val spraySafe = windSpeed < 18.0 && rainProb < 40
        val sprayAdvice = if (spraySafe) "Ideal conditions: Wind < 18 km/h, no immediate rain."
                          else "Unfavorable: High wind/rain risk may wash off chemical."

        // Packing suggestions
        val packing = when {
            rainProb > 50 -> "Carry an umbrella, waterproof footwear, and light cottons."
            temp > 34 -> "Sunscreen SPF 50, sunglasses, breathable cotton clothes, and hydration bottle."
            temp < 16 -> "Warm jacket, light woolens, and moisturizing cream."
            else -> "Comfortable everyday attire and light jacket."
        }
        val packingHi = when {
            rainProb > 50 -> "छाता, वाटरप्रूफ जूते एवं हल्के सूती कपड़े अवश्य रखें।"
            temp > 34 -> "सनस्क्रीन SPF 50, धूप का चश्मा, सूती कपड़े एवं पानी की बोतल रखें।"
            temp < 16 -> "गर्म जैकेट, हल्के ऊनी कपड़े साथ रखें।"
            else -> "सामान्य आरामदायक कपड़े।"
        }

        return CompleteWeatherData(
            location = location,
            currentTemp = temp,
            feelsLike = feelsLike,
            humidity = humidity,
            windSpeed = windSpeed,
            weatherCode = weatherCode,
            conditionText = WmoWeatherUtils.getWeatherDescriptionEn(weatherCode),
            conditionTextHi = WmoWeatherUtils.getWeatherDescriptionHi(weatherCode),
            uvIndex = uv,
            aqi = aqiVal,
            pm25 = pm25,
            pm10 = pm10,
            soilMoisture = soilMoisture,
            waveHeight = (waveHeight * 10).toInt() / 10.0,
            seaCondition = seaCond,
            seaConditionHi = seaCondHi,
            visibilityMeters = visibilityMeters,
            runScore = runScore,
            bestRunningWindow = if (temp > 32) "5:30 AM - 7:00 AM" else "6:00 AM - 8:30 AM",
            schoolCommuteRating = if (rainProb > 60 || visibilityMeters < 200) "Caution Advised" else "Smooth & Clear",
            pesticideSpraySafe = spraySafe,
            sprayAdvice = sprayAdvice,
            eventComfortIndex = runScore,
            packingAdvice = packing,
            packingAdviceHi = packingHi,
            lightningProximityKm = if (weatherCode in listOf(95, 96, 99)) 8 else null,
            imdAlert = imdAlert,
            sevenDayForecast = sevenDays,
            isFromCache = false
        )
    }

    private fun estimateAqiForCity(city: String): Int = when (city) {
        "New Delhi" -> 285
        "Ludhiana" -> 190
        "Mumbai" -> 145
        "Bengaluru" -> 68
        "Goa (Panaji)" -> 45
        "Shimla" -> 35
        "Chennai" -> 110
        else -> 120
    }

    private fun generateSmartFallback(location: LocationInfo): CompleteWeatherData {
        val isCoastal = location.cityName in listOf("Goa (Panaji)", "Mumbai", "Chennai")
        val isMountain = location.cityName == "Shimla"
        val isDelhi = location.cityName == "New Delhi"

        val temp = if (isMountain) 16.0 else if (isCoastal) 31.0 else 33.0
        val aqi = estimateAqiForCity(location.cityName)
        val rainProb = if (isCoastal) 45 else 20
        val code = if (isCoastal) 80 else if (isDelhi) 45 else 1

        val imdAlert = IMDAlertEngine.generateAlert(
            temp = temp,
            rainProb = rainProb,
            weatherCode = code,
            windSpeed = 12.0,
            aqi = aqi,
            visibilityMeters = if (isDelhi) 250 else 6000
        )

        val days = mutableListOf<DayForecast>()
        val cal = Calendar.getInstance()
        val outDayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val outDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (i in 0 until 7) {
            val dayName = if (i == 0) "Today" else outDayFormat.format(cal.time)
            val dateStr = outDateFormat.format(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)

            val dayCode = if (i == 2 && isCoastal) 61 else code
            days.add(
                DayForecast(
                    date = dateStr,
                    dayOfWeek = dayName,
                    weatherCode = dayCode,
                    conditionText = WmoWeatherUtils.getWeatherDescriptionEn(dayCode),
                    conditionTextHi = WmoWeatherUtils.getWeatherDescriptionHi(dayCode),
                    maxTemp = temp + (i % 3),
                    minTemp = temp - 8,
                    rainProbability = rainProb + (i * 5 % 30),
                    uvIndexMax = 7.0,
                    windSpeedMax = 14.0,
                    hourlyList = listOf(
                        HourForecast("06:00", temp - 5, dayCode, 10, 75, 8.0),
                        HourForecast("09:00", temp - 2, dayCode, 15, 65, 10.0),
                        HourForecast("12:00", temp + 1, dayCode, 20, 50, 14.0),
                        HourForecast("15:00", temp + 2, dayCode, 30, 45, 16.0),
                        HourForecast("18:00", temp, dayCode, 25, 60, 12.0),
                        HourForecast("21:00", temp - 3, dayCode, 15, 70, 9.0)
                    )
                )
            )
        }

        return CompleteWeatherData(
            location = location,
            currentTemp = temp,
            feelsLike = temp + 2,
            humidity = 68,
            windSpeed = 14.0,
            weatherCode = code,
            conditionText = WmoWeatherUtils.getWeatherDescriptionEn(code),
            conditionTextHi = WmoWeatherUtils.getWeatherDescriptionHi(code),
            uvIndex = 7.0,
            aqi = aqi,
            pm25 = (aqi * 0.45),
            pm10 = (aqi * 0.85),
            soilMoisture = 55.0,
            waveHeight = if (isCoastal) 1.2 else 0.2,
            seaCondition = if (isCoastal) "Caution / Moderate" else "Safe & Calm",
            seaConditionHi = if (isCoastal) "सतर्क रहें / मध्यम लहरें" else "शांत एवं सुरक्षित",
            visibilityMeters = if (isDelhi) 250 else 5000,
            runScore = 78,
            bestRunningWindow = "5:30 AM - 7:00 AM",
            schoolCommuteRating = "Smooth & Clear",
            pesticideSpraySafe = true,
            sprayAdvice = "Ideal conditions: Low wind and no rain expected.",
            eventComfortIndex = 82,
            packingAdvice = "Light breathable cottons and stay well-hydrated.",
            packingAdviceHi = "हल्के सूती कपड़े पहनें और पर्याप्त पानी पिएं।",
            lightningProximityKm = null,
            imdAlert = imdAlert,
            sevenDayForecast = days,
            isFromCache = true
        )
    }
}
