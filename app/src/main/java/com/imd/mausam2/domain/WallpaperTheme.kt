package com.imd.mausam2.domain

import androidx.compose.ui.graphics.Color

enum class WallpaperTheme(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val dotColor: Color,
    val dayColors: List<Color>,
    val nightColors: List<Color>
) {
    SLATE(
        id = "slate",
        titleEn = "Twilight Slate",
        titleHi = "सांध्य स्लेट",
        dotColor = Color(0xFF93C5FD),
        dayColors = listOf(Color(0xFF182232), Color(0xFF1E2B3E), Color(0xFF202C40), Color(0xFF0C1320)),
        nightColors = listOf(Color(0xFF0B111D), Color(0xFF121B2D), Color(0xFF182236), Color(0xFF080C14))
    ),
    OCEAN(
        id = "ocean",
        titleEn = "Deep Ocean",
        titleHi = "गहरा महासागर",
        dotColor = Color(0xFF38BDF8),
        dayColors = listOf(Color(0xFF0F2642), Color(0xFF16345A), Color(0xFF1D4472), Color(0xFF0A182B)),
        nightColors = listOf(Color(0xFF091728), Color(0xFF0E223B), Color(0xFF152E4F), Color(0xFF050D17))
    ),
    EMERALD(
        id = "emerald",
        titleEn = "Emerald Forest",
        titleHi = "पन्ना वन",
        dotColor = Color(0xFF4ADE80),
        dayColors = listOf(Color(0xFF10281E), Color(0xFF18382B), Color(0xFF204838), Color(0xFF0A1C14)),
        nightColors = listOf(Color(0xFF0A1A13), Color(0xFF10261D), Color(0xFF163428), Color(0xFF050F0B))
    ),
    AMETHYST(
        id = "amethyst",
        titleEn = "Midnight Amethyst",
        titleHi = "गहरी जामुनी",
        dotColor = Color(0xFFA855F7),
        dayColors = listOf(Color(0xFF231638), Color(0xFF301E4D), Color(0xFF3D2762), Color(0xFF140C21)),
        nightColors = listOf(Color(0xFF160E24), Color(0xFF201433), Color(0xFF2A1B43), Color(0xFF0D0817))
    ),
    CHARCOAL(
        id = "charcoal",
        titleEn = "Charcoal Silver",
        titleHi = "चारकोल रजत",
        dotColor = Color(0xFFCBD5E1),
        dayColors = listOf(Color(0xFF1E242B), Color(0xFF272F38), Color(0xFF323B46), Color(0xFF12161A)),
        nightColors = listOf(Color(0xFF13171C), Color(0xFF1C2229), Color(0xFF242C35), Color(0xFF0B0E11))
    ),
    SUNSET(
        id = "sunset",
        titleEn = "Sunset Amber",
        titleHi = "सूर्यास्त अंबर",
        dotColor = Color(0xFFFBBF24),
        dayColors = listOf(Color(0xFF2E1C16), Color(0xFF3E261E), Color(0xFF4E3027), Color(0xFF1C100C)),
        nightColors = listOf(Color(0xFF1F120E), Color(0xFF2B1914), Color(0xFF37211A), Color(0xFF110A07))
    );

    companion object {
        fun fromId(id: String?): WallpaperTheme {
            return values().find { it.id == id } ?: SLATE
        }
    }
}
