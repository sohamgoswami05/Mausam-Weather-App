package com.imd.mausam2.domain

import kotlin.math.roundToInt

object WeatherUnitHelper {

    fun toFahrenheit(celsius: Double): Double {
        return (celsius * 9.0 / 5.0) + 32.0
    }

    fun toMph(kmh: Double): Double {
        return kmh * 0.621371
    }

    fun formatTemp(celsius: Double, isFahrenheit: Boolean): String {
        val temp = if (isFahrenheit) toFahrenheit(celsius) else celsius
        return "${temp.roundToInt()}°"
    }

    fun formatTempWithUnit(celsius: Double, isFahrenheit: Boolean): String {
        val temp = if (isFahrenheit) toFahrenheit(celsius) else celsius
        val unit = if (isFahrenheit) "°F" else "°C"
        return "${temp.roundToInt()}$unit"
    }

    fun formatSpeed(kmh: Double, isFahrenheit: Boolean): String {
        return if (isFahrenheit) {
            "${toMph(kmh).roundToInt()} mph"
        } else {
            "${kmh.roundToInt()} km/h"
        }
    }

    fun formatRange(minC: Double, maxC: Double, isFahrenheit: Boolean): String {
        val min = formatTemp(minC, isFahrenheit)
        val max = formatTemp(maxC, isFahrenheit)
        return "H $max L $min"
    }
}
