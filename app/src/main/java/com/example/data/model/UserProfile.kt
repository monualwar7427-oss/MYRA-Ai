package com.example.data.model

data class UserProfile(
    val userName: String = "राहुल शर्मा",
    val userBio: String = "MYRA AI एक्सप्लोरर",
    val avatarId: String = "avatar_tech",
    val backgroundThemeId: String = "theme_cosmic",
    val aiTone: String = "दोस्ताना व मधुर",
    val languageMode: String = "हिन्दी",
    val voiceSpeed: Float = 1.0f,
    val voicePitch: Float = 1.0f,
    val autoSpeakResponse: Boolean = true,
    val customInstructions: String = "सरल और स्पष्ट भाषा में उदाहरण सहित समझाएं।"
)

data class AvatarOption(
    val id: String,
    val name: String,
    val emoji: String,
    val hexColor: Long
)

data class ThemeOption(
    val id: String,
    val name: String,
    val hindiName: String,
    val gradientColors: List<Long>
)
