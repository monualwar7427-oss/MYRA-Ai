package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AvatarOption
import com.example.data.model.ThemeOption
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("myra_user_profile", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    companion object {
        val AVATAR_OPTIONS = listOf(
            AvatarOption("avatar_tech", "टेक गुरु", "👨‍💻", 0xFF00E5FF),
            AvatarOption("avatar_boy_1", "युवा साथी", "👦", 0xFF0072FF),
            AvatarOption("avatar_girl_1", "अन्वेषक", "👧", 0xFFFF2A85),
            AvatarOption("avatar_seeker", "अध्यात्म साधक", "🧘", 0xFFFFD166),
            AvatarOption("avatar_scholar", "जिज्ञासु छात्र", "🎓", 0xFF9D4EDD),
            AvatarOption("avatar_creative", "कलाकार", "🎨", 0xFF10B981),
            AvatarOption("avatar_robot_friend", "साइबर साथी", "🤖", 0xFF00B4D8),
            AvatarOption("avatar_royal", "वीआईपी यूजर", "👑", 0xFFF59E0B)
        )

        val THEME_OPTIONS = listOf(
            ThemeOption(
                id = "theme_cosmic",
                name = "Cosmic Dark",
                hindiName = "कॉस्मिक डार्क",
                gradientColors = listOf(0xFF060B18, 0xFF0B142E, 0xFF140D2F)
            ),
            ThemeOption(
                id = "theme_neon",
                name = "Cyberpunk Violet",
                hindiName = "साइबरपंक वॉयलेट",
                gradientColors = listOf(0xFF0F051D, 0xFF1A0933, 0xFF2D0B4E)
            ),
            ThemeOption(
                id = "theme_aurora",
                name = "Emerald Aurora",
                hindiName = "एमराल्ड ऑरोरा",
                gradientColors = listOf(0xFF021712, 0xFF062E24, 0xFF0A4436)
            ),
            ThemeOption(
                id = "theme_royal",
                name = "Royal Sapphire",
                hindiName = "रॉयल सफायर",
                gradientColors = listOf(0xFF041026, 0xFF0B204C, 0xFF103373)
            ),
            ThemeOption(
                id = "theme_sunset",
                name = "Twilight Crimson",
                hindiName = "ट्वाइलाइट क्रिम्सन",
                gradientColors = listOf(0xFF190913, 0xFF2B0E20, 0xFF45142E)
            )
        )

        val AI_TONE_OPTIONS = listOf(
            "दोस्ताना व मधुर" to "मित्रवत, आत्मीय और सरल शब्दों में बातचीत",
            "शिक्षक व मार्गदर्शक" to "गहराई से समझाने वाला और उदाहरणों से सिखाने वाला",
            "संक्षिप्त व सटीक" to "सीधा बिंदु पर, त्वरित और सटीक जवाब",
            "काव्यात्मक व भक्तिमय" to "शालीन, सम्मानजनक और प्रेरणादायक भाषा शैली"
        )

        val LANGUAGE_OPTIONS = listOf(
            "हिन्दी" to "प्राकृतिक शुद्ध व आम बोलचाल की हिंदी",
            "हिंग्लिश" to "हिंदी और इंग्लिश शब्दों का मिला-जुला रूप",
            "English" to "Global international English response"
        )
    }

    private fun loadProfile(): UserProfile {
        return UserProfile(
            userName = prefs.getString("user_name", "राहुल शर्मा") ?: "राहुल शर्मा",
            userBio = prefs.getString("user_bio", "MYRA AI एक्सप्लोरर") ?: "MYRA AI एक्सप्लोरर",
            avatarId = prefs.getString("avatar_id", "avatar_tech") ?: "avatar_tech",
            backgroundThemeId = prefs.getString("theme_id", "theme_cosmic") ?: "theme_cosmic",
            aiTone = prefs.getString("ai_tone", "दोस्ताना व मधुर") ?: "दोस्ताना व मधुर",
            languageMode = prefs.getString("language_mode", "हिन्दी") ?: "हिन्दी",
            voiceSpeed = prefs.getFloat("voice_speed", 1.0f),
            voicePitch = prefs.getFloat("voice_pitch", 1.0f),
            autoSpeakResponse = prefs.getBoolean("auto_speak", true),
            customInstructions = prefs.getString(
                "custom_instructions",
                "सरल और स्पष्ट भाषा में उदाहरण सहित समझाएं।"
            ) ?: "सरल और स्पष्ट भाषा में उदाहरण सहित समझाएं।"
        )
    }

    fun saveProfile(profile: UserProfile) {
        prefs.edit()
            .putString("user_name", profile.userName)
            .putString("user_bio", profile.userBio)
            .putString("avatar_id", profile.avatarId)
            .putString("theme_id", profile.backgroundThemeId)
            .putString("ai_tone", profile.aiTone)
            .putString("language_mode", profile.languageMode)
            .putFloat("voice_speed", profile.voiceSpeed)
            .putFloat("voice_pitch", profile.voicePitch)
            .putBoolean("auto_speak", profile.autoSpeakResponse)
            .putString("custom_instructions", profile.customInstructions)
            .apply()

        _userProfile.value = profile
    }
}
