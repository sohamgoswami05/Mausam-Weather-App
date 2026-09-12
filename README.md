# Mausam 2.0 — Personalized Homepage Mobile App
### Smart India Hackathon (SIH) | Problem Statement ID: 26076
**Ministry of Earth Sciences (MoES) | India Meteorological Department (IMD)**

---

## 🌦️ Overview
**Mausam 2.0** reimagines the official India Meteorological Department mobile experience from an outdated text/tabular data repository into a modern, actionable, persona-driven frosted-glass weather intelligence hub with 7-day offline caching.

---

## ✨ Key Features

1. **🎨 Frosted Glass Aesthetic (Option A)**
   - Ambient dynamic atmospheric gradient background reacting to real-time weather codes (Clear Sky, Severe Thunderstorm, Dense Fog, Monsoonal Downpour).
   - Polished translucent frosted glass cards (`backdrop-blur`) with subtle border glows.

2. **⚡ Top Scrolling Critical Weather Alerts Ticker**
   - High-priority IMD Nowcast bulletin ribbon at the very top of the homepage with pulsing live status.

3. **🎯 Personalized Consumer Persona Cards**
   - **🏖️ Beachgoers & Coastal Citizens**: Swell and wave height, Sea Safety Flag status (Safe / Caution / Dangerous), 24-hour sinusoidal tide cycle curve, and coastal water temperature.
   - **🏃 Outdoor Fitness Enthusiasts**: Workout suitability score (0–100), optimal running hours window, and heat stress alerts.
   - **🫁 Health-Conscious**: AQI gauge (Good to Severe), PM2.5/PM10 pollutant breakdown, UV Index with sunscreen SPF recommendation, and Asthma/Allergy advisories.
   - **🌾 Agriculture & Kisan**: Soil moisture (0–10cm root depth), pesticide/fertilizer spray window check, and IMD Agromet / Meghdoot crop bulletins.
   - **🚗 Daily Commuters & School Parents**: Road visibility meter (meters), School Commute Risk Window (07:00 AM – 09:00 AM), and localized waterlogging risk alerts.
   - **✈️ Travelers & Event Planners**: Smart Packing Suggestion Assistant and flight/transit weather delay risk.
   - **⚡ Severe Storm Watchers**: Damini Lightning Early Warning Sensor (proximity in km), Doppler radar status, and direct Disaster Management Helpline shortcuts (1070 / 112).

4. **📱 4 Frosted Bottom Navigation Tabs**
   - 🏠 **Home**: Ticker + Hero card + 7-Day Forecast Ribbon + Selected priority cards feed.
   - 🗺️ **Radar**: Interactive animated IMD Doppler Radar simulation with rotating 360° sweep beam and Damini lightning sensor grid.
   - 🔔 **Alerts**: Official 4-Stage Warning Center (Green, Yellow, Orange, Red) with district advisories.
   - 👤 **Profiles & Tools**: Instant persona switcher, Traveler's Smart Packing Assistant, and card customizer.

5. **🌐 Bilingual & Audio Sahayak**
   - 1-tap toggle between **English** and **हिन्दी**.
   - **Mausam Audio Sahayak**: Android native Text-to-Speech (TTS) engine that speaks the morning weather brief aloud with animated sound waves.

---

## 🛠️ Tech Stack
- **Language**: Kotlin 2.0
- **UI Toolkit**: Jetpack Compose & Material 3
- **Architecture**: MVVM with Kotlin Coroutines & StateFlow
- **Networking**: Retrofit 2 + OkHttp + Gson
- **Location**: Google Play Services FusedLocationProviderClient + Geocoder
- **Data Source**: Open-Meteo Free APIs (7-day hourly & daily forecast, Air Quality, Marine) + IMD Alert Engine
