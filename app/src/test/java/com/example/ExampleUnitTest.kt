package com.example

import com.example.data.model.CategoryItem
import com.example.data.model.ChatMessage
import com.example.data.model.SearchSource
import com.example.data.model.UserProfile
import com.example.data.model.WeatherCardData
import com.example.data.network.GoogleSearchTool
import com.example.data.network.Tool
import com.example.data.repository.ProfileRepository
import com.example.ui.viewmodel.VoiceSessionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testChatMessageCreationWithSearchGrounding() {
        val weather = WeatherCardData(
            condition = "Sunny (हल्का बादल)",
            maxTemp = "32°C",
            minTemp = "22°C",
            iconEmoji = "☀️"
        )
        val source = SearchSource(
            title = "Indian Meteorological Department",
            url = "https://mausam.imd.gov.in"
        )
        val msg = ChatMessage(
            text = "आज आपके शहर में मौसम आम तौर पर साफ रहेगा।",
            isUser = false,
            audioDurationSeconds = 32,
            weatherData = weather,
            category = "Weather",
            searchQueries = listOf("आज का मौसम पूर्वानुमान"),
            searchSources = listOf(source),
            isGrounded = true
        )

        assertEquals("Weather", msg.category)
        assertEquals(32, msg.audioDurationSeconds)
        assertNotNull(msg.weatherData)
        assertEquals("32°C", msg.weatherData?.maxTemp)
        assertTrue(msg.isGrounded)
        assertEquals(1, msg.searchSources.size)
        assertEquals("Indian Meteorological Department", msg.searchSources.first().title)
    }

    @Test
    fun testGoogleSearchToolCreation() {
        val searchTool = Tool(googleSearch = GoogleSearchTool())
        assertNotNull(searchTool.googleSearch)
    }

    @Test
    fun testCategoryItemValidation() {
        val category = CategoryItem(
            id = "gk",
            title = "General Knowledge",
            hindiTitle = "सामान्य ज्ञान",
            iconEmoji = "💡",
            samplePrompts = listOf("भारत का संविधान कब लागू हुआ था?")
        )

        assertEquals("General Knowledge", category.title)
        assertEquals("सामान्य ज्ञान", category.hindiTitle)
        assertTrue(category.samplePrompts.isNotEmpty())
    }

    @Test
    fun testUserProfileDefaultsAndCustomization() {
        val profile = UserProfile(
            userName = "विक्रम सिंह",
            userBio = "एआई विद्यार्थी",
            avatarId = "avatar_scholar",
            backgroundThemeId = "theme_aurora",
            aiTone = "शिक्षक व मार्गदर्शक",
            languageMode = "हिन्दी",
            voiceSpeed = 1.1f,
            voicePitch = 0.95f,
            autoSpeakResponse = true,
            customInstructions = "हमेशा सरल भाषा में उदाहरण सहित समझाएं।"
        )

        assertEquals("विक्रम सिंह", profile.userName)
        assertEquals("avatar_scholar", profile.avatarId)
        assertEquals("theme_aurora", profile.backgroundThemeId)
        assertEquals("शिक्षक व मार्गदर्शक", profile.aiTone)
        assertTrue(profile.autoSpeakResponse)

        assertTrue(ProfileRepository.AVATAR_OPTIONS.size >= 8)
        assertTrue(ProfileRepository.THEME_OPTIONS.size >= 5)
        assertTrue(ProfileRepository.AI_TONE_OPTIONS.isNotEmpty())
        assertTrue(ProfileRepository.LANGUAGE_OPTIONS.isNotEmpty())
    }

    @Test
    fun testVoiceSessionStateAndLanguageDetection() {
        val state = VoiceSessionState.LISTENING
        assertEquals(VoiceSessionState.LISTENING, state)

        val hindiText = "नमस्ते, आज का मौसम कैसा है?"
        val isHindi = hindiText.any { it in '\u0900'..'\u097F' }
        assertTrue("Devanagari script should be identified as Hindi", isHindi)

        val englishText = "Hello Myra, what is the distance to the moon?"
        val isEnglishOnly = englishText.none { it in '\u0900'..'\u097F' }
        assertTrue("Latin text should not be identified as Hindi", isEnglishOnly)
    }

    @Test
    fun testTranscriptExporterFormatting() {
        val messages = listOf(
            ChatMessage(
                text = "आज का मौसम कैसा है?",
                isUser = true,
                formattedTime = "10:30 AM"
            ),
            ChatMessage(
                text = "आज मौसम सुहावना रहेगा और हल्की धूप खिलेगी।",
                isUser = false,
                formattedTime = "10:31 AM",
                weatherData = WeatherCardData("Sunny", "32°C", "22°C", "☀️")
            )
        )

        val transcript = com.example.ui.util.TranscriptExporter.formatTranscript(messages, "मोहित शर्मा")
        assertTrue(transcript.contains("MYRA AI - Chat Transcript"))
        assertTrue(transcript.contains("User: मोहित शर्मा"))
        assertTrue(transcript.contains("Total Messages: 2"))
        assertTrue(transcript.contains("[10:30 AM] मोहित शर्मा:"))
        assertTrue(transcript.contains("आज का मौसम कैसा है?"))
        assertTrue(transcript.contains("[10:31 AM] MYRA AI:"))
        assertTrue(transcript.contains("आज मौसम सुहावना रहेगा"))
        assertTrue(transcript.contains("[मौसम कार्ड: Sunny | 32°C / 22°C]"))
        assertTrue(transcript.contains("Generated via MYRA AI"))
    }

    @Test
    fun testTranscriptExporterFileNameGeneration() {
        val fileName = com.example.ui.util.TranscriptExporter.generateFileName()
        assertTrue(fileName.startsWith("MYRA_Chat_Transcript_"))
        assertTrue(fileName.endsWith(".txt"))
    }
}
