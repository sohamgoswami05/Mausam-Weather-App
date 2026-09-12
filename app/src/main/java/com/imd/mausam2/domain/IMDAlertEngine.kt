package com.imd.mausam2.domain

import com.imd.mausam2.data.model.IMDAlert
import com.imd.mausam2.data.model.IMDWarningLevel

object WmoWeatherUtils {
    fun getWeatherDescriptionEn(code: Int): String = when (code) {
        0 -> "Clear Sky"
        1 -> "Mainly Clear"
        2 -> "Partly Cloudy"
        3 -> "Overcast"
        45, 48 -> "Foggy Conditions"
        51, 53, 55 -> "Light Drizzle"
        61, 63 -> "Moderate Rain"
        65 -> "Heavy Downpour"
        71, 73, 75 -> "Snowfall"
        80, 81, 82 -> "Rain Showers"
        95 -> "Thunderstorm"
        96, 99 -> "Severe Thunderstorm & Hail"
        else -> "Variable Weather"
    }

    fun getWeatherDescriptionHi(code: Int): String = when (code) {
        0 -> "साफ़ आसमान"
        1 -> "अधिकांशतः साफ़"
        2 -> "आंशिक रूप से बादल"
        3 -> "घने बादल"
        45, 48 -> "कोहरा / धुंध"
        51, 53, 55 -> "हल्की बूंदाबांदी"
        61, 63 -> "मध्यम वर्षा"
        65 -> "भारी बारिश"
        71, 73, 75 -> "बर्फबारी"
        80, 81, 82 -> "तेज़ बौछारें"
        95 -> "गरज के साथ बारिश"
        96, 99 -> "भारी तूफ़ान एवं ओलावृष्टि"
        else -> "परिवर्तनशील मौसम"
    }
}

object IMDAlertEngine {

    fun generateAlert(
        temp: Double,
        rainProb: Int,
        weatherCode: Int,
        windSpeed: Double,
        aqi: Int,
        visibilityMeters: Int
    ): IMDAlert {
        return when {
            // RED ALERT CONDITIONS
            temp >= 44.0 -> IMDAlert(
                level = IMDWarningLevel.RED,
                headline = "Severe Heatwave Warning (IMD Red Alert)",
                headlineHi = "भीषण लू की चेतावनी (आईएमडी रेड अलर्ट)",
                description = "Extreme heat condition. High risk of heatstroke for all age groups.",
                descriptionHi = "अत्यधिक गर्मी की स्थिति। सभी आयु वर्गों के लिए लू लगने का भारी जोखिम।",
                advice = "Avoid direct sunlight between 11 AM - 4 PM. Drink ORS and stay indoors.",
                adviceHi = "सुबह 11 से शाम 4 बजे तक धूप से बचें। ओआरएस पिएं और घर के अंदर रहें।"
            )
            weatherCode in listOf(95, 96, 99) && rainProb >= 75 -> IMDAlert(
                level = IMDWarningLevel.RED,
                headline = "Severe Thunderstorm & Squall Alert (Red)",
                headlineHi = "भीषण आंधी-तूफ़ान एवं वज्रपात चेतावनी (रेड)",
                description = "Severe squall with frequent lightning strikes and potential hail.",
                descriptionHi = "तीव्र हवाओं के साथ भारी वज्रपात और ओलावृष्टि की संभावना।",
                advice = "Stay away from open fields and tin sheds. Disconnect electrical appliances.",
                adviceHi = "खुले मैदानों और पेड़ों से दूर रहें। बिजली के उपकरणों को अनप्लग करें।"
            )
            aqi >= 400 -> IMDAlert(
                level = IMDWarningLevel.RED,
                headline = "Hazardous Air Quality Emergency (Red Alert)",
                headlineHi = "गंभीर वायु प्रदूषण आपातकाल (रेड अलर्ट)",
                description = "AQI exceeds 400. Severe health impact on respiratory systems.",
                descriptionHi = "वायु गुणवत्ता सूचकांक 400 के पार। सांस के रोगियों के लिए आपातकाल।",
                advice = "Mandatory N95 mask outdoors. Avoid all vigorous outdoor physical exercise.",
                adviceHi = "बाहर जाते समय N95 मास्क अवश्य पहनें। बाहरी व्यायाम पूर्णतः बंद रखें।"
            )

            // ORANGE ALERT CONDITIONS
            temp >= 41.0 -> IMDAlert(
                level = IMDWarningLevel.ORANGE,
                headline = "Heatwave Alert - Be Prepared (Orange)",
                headlineHi = "लू की चेतावनी - तैयार रहें (ऑरेंज अलर्ट)",
                description = "High temperatures likely to cause discomfort for infants and elderly.",
                descriptionHi = "अधिक तापमान से बच्चों और बुजुर्गों में स्वास्थ्य समस्या संभव।",
                advice = "Wear light cotton clothing and carry water bottles during commute.",
                adviceHi = "हल्के सूती कपड़े पहनें और यात्रा के दौरान पर्याप्त पानी साथ रखें।"
            )
            weatherCode in listOf(65, 81, 82, 95) || rainProb >= 60 -> IMDAlert(
                level = IMDWarningLevel.ORANGE,
                headline = "Heavy Rainfall & Thunder Watch (Orange)",
                headlineHi = "भारी बारिश एवं आंधी चेतावनी (ऑरेंज अलर्ट)",
                description = "Localized waterlogging and traffic congestion expected.",
                descriptionHi = "स्थानीय जलभराव और यातायात में बाधा की संभावना।",
                advice = "Plan your commute with buffer time. Keep umbrella/raincoat handy.",
                adviceHi = "सफर के लिए अतिरिक्त समय लेकर निकलें। छाता या रेनकोट साथ रखें।"
            )
            visibilityMeters < 150 -> IMDAlert(
                level = IMDWarningLevel.ORANGE,
                headline = "Dense Fog Advisory (Orange Alert)",
                headlineHi = "घने कोहरे की चेतावनी (ऑरेंज अलर्ट)",
                description = "Road visibility critically impaired below 150 meters.",
                descriptionHi = "सड़क पर दृश्यता 150 मीटर से कम, दुर्घटना की आशंका।",
                advice = "Use low-beam fog lights and maintain safe distance between vehicles.",
                adviceHi = "वाहन में फॉग लाइट का प्रयोग करें और गति धीमी रखें।"
            )
            aqi >= 300 -> IMDAlert(
                level = IMDWarningLevel.ORANGE,
                headline = "Very Poor Air Quality Alert (Orange)",
                headlineHi = "बहुत खराब वायु गुणवत्ता (ऑरेंज अलर्ट)",
                description = "Respiratory illness possible on prolonged exposure.",
                descriptionHi = "लंबे समय तक बाहर रहने पर सांस लेने में तकलीफ़ हो सकती है।",
                advice = "Wear masks outdoors and limit outdoor playtime for children.",
                adviceHi = "बाहर मास्क पहनें और बच्चों को घर के अंदर खेलने दें।"
            )

            // YELLOW ALERT CONDITIONS
            rainProb >= 35 || weatherCode in listOf(51, 53, 61, 80) -> IMDAlert(
                level = IMDWarningLevel.YELLOW,
                headline = "Weather Watch: Scattered Showers (Yellow)",
                headlineHi = "मौसम निगरानी: छिटपुट बारिश (येलो अलर्ट)",
                description = "Light to moderate rain possible during morning/evening commute.",
                descriptionHi = "सुबह या शाम के समय हल्की से मध्यम बारिश की संभावना।",
                advice = "Stay updated with local nowcast bulletins.",
                adviceHi = "स्थानीय मौसम अपडेट्स से अवगत रहें।"
            )
            aqi >= 200 -> IMDAlert(
                level = IMDWarningLevel.YELLOW,
                headline = "Moderate to Poor AQI (Yellow)",
                headlineHi = "मध्यम से खराब AQI (येलो अलर्ट)",
                description = "Sensitive individuals may experience breathing discomfort.",
                descriptionHi = "अस्थमा और एलर्जी से पीड़ित लोगों को परेशानी हो सकती है।",
                advice = "Keep inhalers handy and monitor symptoms.",
                adviceHi = "इनहेलर साथ रखें और स्वास्थ्य पर ध्यान दें।"
            )

            // GREEN ALERT (NORMAL)
            else -> IMDAlert(
                level = IMDWarningLevel.GREEN,
                headline = "Normal Weather Conditions (Green)",
                headlineHi = "सामान्य मौसम स्थिति (हरा - सुरक्षित)",
                description = "No severe weather warnings. Ideal conditions for outdoor activities.",
                descriptionHi = "कोई गंभीर मौसम चेतावनी नहीं। बाहरी गतिविधियों के लिए अनुकूल।",
                advice = "Enjoy your day! Safe for outdoor travel and fitness.",
                adviceHi = "आपका दिन शुभ हो! यात्रा और सैर के लिए सुरक्षित मौसम।"
            )
        }
    }
}
