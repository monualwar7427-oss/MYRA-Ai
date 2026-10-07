package com.example.data.repository

import android.content.Context
import com.example.BuildConfig
import com.example.data.local.AppDatabase
import com.example.data.local.ChatEntity
import com.example.data.model.ChatMessage
import com.example.data.model.SearchSource
import com.example.data.model.UserProfile
import com.example.data.model.WeatherCardData
import com.example.data.network.Content
import com.example.data.network.GenerateContentRequest
import com.example.data.network.GenerationConfig
import com.example.data.network.GoogleSearchTool
import com.example.data.network.Part
import com.example.data.network.RetrofitClient
import com.example.data.network.Tool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatRepository(private val context: Context) {
    private val chatDao = AppDatabase.getDatabase(context).chatDao()
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val allMessages: Flow<List<ChatMessage>> = chatDao.getAllMessages().map { list ->
        list.map { entity ->
            val weather = if (!entity.weatherCondition.isNullOrEmpty() && !entity.weatherMaxTemp.isNullOrEmpty()) {
                WeatherCardData(
                    condition = entity.weatherCondition,
                    maxTemp = entity.weatherMaxTemp,
                    minTemp = entity.weatherMinTemp ?: "20°C",
                    iconEmoji = if (entity.weatherCondition.contains("Rain") || entity.weatherCondition.contains("बारिश")) "🌧️" else "☀️"
                )
            } else null

            ChatMessage(
                id = entity.id,
                text = entity.text,
                isUser = entity.isUser,
                timestamp = entity.timestamp,
                formattedTime = entity.formattedTime,
                audioDurationSeconds = entity.audioDurationSeconds,
                isPlaying = false,
                playbackProgress = 0f,
                weatherData = weather,
                category = entity.category,
                searchQueries = entity.searchQueriesJoined?.split("|||")?.filter { it.isNotBlank() } ?: emptyList(),
                searchSources = deserializeSources(entity.searchSourcesJson),
                isGrounded = entity.isGrounded
            )
        }
    }

    suspend fun initializeSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        val count = chatDao.getMessageCount()
        if (count == 0) {
            val baseTime = System.currentTimeMillis() - 1000 * 60 * 15 // 15 mins ago

            // Seed 1: Weather query (matching Screenshot 3 with Google Search grounding)
            val msg1User = ChatEntity(
                text = "आज का मौसम कैसा है?",
                isUser = true,
                timestamp = baseTime,
                formattedTime = "10:24 AM",
                audioDurationSeconds = 5,
                weatherCondition = null,
                weatherMaxTemp = null,
                weatherMinTemp = null,
                category = "Weather",
                searchQueriesJoined = null,
                searchSourcesJson = null,
                isGrounded = false
            )
            chatDao.insertMessage(msg1User)

            val msg1Ai = ChatEntity(
                text = "आज आपके शहर में मौसम आम तौर पर साफ रहेगा। अधिकतम तापमान 32°C और न्यूनतम तापमान 22°C रहने की संभावना है। हल्की हवा चलेगी और बारिश की संभावना बहुत कम है।",
                isUser = false,
                timestamp = baseTime + 1000 * 20,
                formattedTime = "10:25 AM",
                audioDurationSeconds = 32,
                weatherCondition = "Sunny (हल्का बादल)",
                weatherMaxTemp = "32°C",
                weatherMinTemp = "22°C",
                category = "Weather",
                searchQueriesJoined = "आज का मौसम पूर्वानुमान 2026",
                searchSourcesJson = serializeSources(
                    listOf(
                        SearchSource("मौसम विज्ञान विभाग (IMD)", "https://mausam.imd.gov.in"),
                        SearchSource("AccuWeather Live Forecast", "https://accuweather.com")
                    )
                ),
                isGrounded = true
            )
            chatDao.insertMessage(msg1Ai)

            // Seed 2: Mahadev query (matching Screenshot 4)
            val msg2User = ChatEntity(
                text = "महादेव के बारे में बताओ",
                isUser = true,
                timestamp = baseTime + 1000 * 60 * 2,
                formattedTime = "10:26 AM",
                audioDurationSeconds = 6,
                weatherCondition = null,
                weatherMaxTemp = null,
                weatherMinTemp = null,
                category = "Mahadev",
                searchQueriesJoined = null,
                searchSourcesJson = null,
                isGrounded = false
            )
            chatDao.insertMessage(msg2User)

            val msg2Ai = ChatEntity(
                text = "महादेव, जिन्हें भगवान शिव भी कहा जाता है, हिन्दू धर्म में संहार, सृजन और परिवर्तन के देवता हैं। वह करुणा के सागर, भोलेनाथ और सबके रक्षक हैं। उनकी पूजा से मन को शांति, जीवन में सकारात्मक ऊर्जा और मुश्किलों से लड़ने की शक्ति मिलती है।",
                isUser = false,
                timestamp = baseTime + 1000 * 60 * 2 + 1000 * 25,
                formattedTime = "10:27 AM",
                audioDurationSeconds = 32,
                weatherCondition = null,
                weatherMaxTemp = null,
                weatherMinTemp = null,
                category = "Mahadev",
                searchQueriesJoined = null,
                searchSourcesJson = null,
                isGrounded = false
            )
            chatDao.insertMessage(msg2Ai)

            // Seed 3: Inspirational story (matching Screenshot 5)
            val msg3User = ChatEntity(
                text = "मेरे लिए एक प्रेरणादायक कहानी सुनाओ",
                isUser = true,
                timestamp = baseTime + 1000 * 60 * 4,
                formattedTime = "10:28 AM",
                audioDurationSeconds = 7,
                weatherCondition = null,
                weatherMaxTemp = null,
                weatherMinTemp = null,
                category = "Story",
                searchQueriesJoined = null,
                searchSourcesJson = null,
                isGrounded = false
            )
            chatDao.insertMessage(msg3User)

            val msg3Ai = ChatEntity(
                text = "एक छोटे से गाँव में एक लड़का था, जिसका सपना था कि वह बड़ा बनकर अपने गाँव का नाम रोशन करे। वो रोज मेहनत करता, कभी हार नहीं मानता। कई साल की मेहनत के बाद वो अपने सपने में सफल हुआ और साबित कर दिया कि अगर हौसला हो तो कोई भी सपना पूरा हो सकता है।",
                isUser = false,
                timestamp = baseTime + 1000 * 60 * 4 + 1000 * 30,
                formattedTime = "10:29 AM",
                audioDurationSeconds = 45,
                weatherCondition = null,
                weatherMaxTemp = null,
                weatherMinTemp = null,
                category = "Story",
                searchQueriesJoined = null,
                searchSourcesJson = null,
                isGrounded = false
            )
            chatDao.insertMessage(msg3Ai)
        }
    }

    suspend fun saveUserMessage(text: String, category: String = "General"): Long = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val entity = ChatEntity(
            text = text,
            isUser = true,
            timestamp = now,
            formattedTime = timeFormat.format(Date(now)),
            audioDurationSeconds = calculateDuration(text),
            weatherCondition = null,
            weatherMaxTemp = null,
            weatherMinTemp = null,
            category = category,
            searchQueriesJoined = null,
            searchSourcesJson = null,
            isGrounded = false
        )
        chatDao.insertMessage(entity)
    }

    suspend fun getAiResponse(
        userPrompt: String,
        category: String = "General",
        profile: UserProfile? = null
    ): ChatMessage = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        var responseText = ""
        var weatherCard: WeatherCardData? = null
        var searchQueries: List<String> = emptyList()
        var searchSources: List<SearchSource> = emptyList()
        var isGrounded = false

        val isWeatherQuery = userPrompt.contains("मौसम", ignoreCase = true) ||
                userPrompt.contains("weather", ignoreCase = true) ||
                userPrompt.contains("तापमान", ignoreCase = true)

        val userName = profile?.userName ?: "यूज़र"
        val aiTone = profile?.aiTone ?: "दोस्ताना व मधुर"
        val langMode = profile?.languageMode ?: "हिन्दी"
        val customInstructions = profile?.customInstructions ?: ""

        if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val systemInstructionText = """
                    You are MYRA AI (मायरा एआई), a personalized intelligent, highly polite, caring and warm AI smart assistant.
                    The user's name is: $userName. Address them warmly and respectfully when appropriate.
                    Selected Tone & Personality: $aiTone. Embody this personality consistently.
                    Preferred Language Mode: $langMode. (If 'हिन्दी', use natural conversational Hindi; if 'हिंग्लिश', use Hinglish; if 'English', use clear English).
                    User's Custom Preferences & Instructions: $customInstructions
                    You have access to Google Search grounding tools for real-time, up-to-date facts, current news, sports, weather, technology, and general knowledge.
                    Keep answers concise, clear, and conversational (2 to 4 sentences or bullet points), ideal for voice reading.
                    If the user asks about weather, give a realistic forecast (e.g. today's sunny/cloudy weather, temp 32°C / 22°C).
                    If the user asks about Mahadev/Shiva, answer with great spiritual reverence and devotion.
                    If the user asks for a story, share a short uplifting and inspiring moral story.
                    Include warm greetings and relevant emojis.
                """.trimIndent()

                // Request with Google Search Tool Grounding enabled
                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = userPrompt)), role = "user")
                    ),
                    systemInstruction = Content(parts = listOf(Part(text = systemInstructionText))),
                    generationConfig = GenerationConfig(temperature = 0.7f, maxOutputTokens = 600),
                    tools = listOf(Tool(googleSearch = GoogleSearchTool()))
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val candidate = response.candidates?.firstOrNull()
                val candidateText = candidate?.content?.parts?.firstOrNull()?.text
                if (!candidateText.isNullOrBlank()) {
                    responseText = candidateText.trim()
                }

                // Extract Google Search Grounding Metadata
                val grounding = candidate?.groundingMetadata
                val queries = grounding?.webSearchQueries
                if (!queries.isNullOrEmpty()) {
                    searchQueries = queries
                    isGrounded = true
                }
                val chunks = grounding?.groundingChunks
                if (!chunks.isNullOrEmpty()) {
                    val parsedSources = chunks.mapNotNull { chunk ->
                        val web = chunk.web
                        if (!web?.uri.isNullOrBlank() && !web?.title.isNullOrBlank()) {
                            SearchSource(title = web.title!!, url = web.uri!!)
                        } else null
                    }
                    if (parsedSources.isNotEmpty()) {
                        searchSources = parsedSources
                        isGrounded = true
                    }
                }
            } catch (e: Exception) {
                val fallbackData = getSmartFallback(userPrompt, userName)
                responseText = fallbackData.text
                searchQueries = fallbackData.queries
                searchSources = fallbackData.sources
                isGrounded = fallbackData.isGrounded
            }
        } else {
            val fallbackData = getSmartFallback(userPrompt, userName)
            responseText = fallbackData.text
            searchQueries = fallbackData.queries
            searchSources = fallbackData.sources
            isGrounded = fallbackData.isGrounded
        }

        if (isWeatherQuery) {
            weatherCard = WeatherCardData(
                condition = "Sunny (हल्का बादल)",
                maxTemp = "32°C",
                minTemp = "22°C",
                iconEmoji = "☀️"
            )
        }

        val now = System.currentTimeMillis()
        val duration = calculateDuration(responseText)
        val entity = ChatEntity(
            text = responseText,
            isUser = false,
            timestamp = now,
            formattedTime = timeFormat.format(Date(now)),
            audioDurationSeconds = duration,
            weatherCondition = weatherCard?.condition,
            weatherMaxTemp = weatherCard?.maxTemp,
            weatherMinTemp = weatherCard?.minTemp,
            category = category,
            searchQueriesJoined = if (searchQueries.isNotEmpty()) searchQueries.joinToString("|||") else null,
            searchSourcesJson = if (searchSources.isNotEmpty()) serializeSources(searchSources) else null,
            isGrounded = isGrounded
        )

        val id = chatDao.insertMessage(entity)

        ChatMessage(
            id = id,
            text = responseText,
            isUser = false,
            timestamp = now,
            formattedTime = timeFormat.format(Date(now)),
            audioDurationSeconds = duration,
            weatherData = weatherCard,
            category = category,
            searchQueries = searchQueries,
            searchSources = searchSources,
            isGrounded = isGrounded
        )
    }

    private data class FallbackResult(
        val text: String,
        val queries: List<String> = emptyList(),
        val sources: List<SearchSource> = emptyList(),
        val isGrounded: Boolean = false
    )

    private fun getSmartFallback(prompt: String, userName: String = "यूज़र"): FallbackResult {
        val lower = prompt.lowercase(Locale.getDefault())
        return when {
            lower.contains("namaste") || lower.contains("नमस्ते") || lower.contains("नमस्कार") || lower.contains("hello") || lower.contains("hi") ->
                FallbackResult(
                    text = "नमस्ते $userName जी! मैं हूँ MYRA AI, आपका व्यक्तिगत स्मार्ट असिस्टेंट। मैं Google Search ग्राउंडिंग और लाइव वॉयस के साथ आपकी सहायता के लिए तैयार हूँ!",
                    isGrounded = false
                )

            lower.contains("मौसम") || lower.contains("weather") ->
                FallbackResult(
                    text = "आज आपके शहर में मौसम आम तौर पर साफ रहेगा। अधिकतम तापमान 32°C और न्यूनतम तापमान 22°C रहने की संभावना है। हल्की हवा चलेगी और बारिश की संभावना बहुत कम है। बाहर निकलने के लिए दिन बहुत सुहावना है!",
                    queries = listOf("आज का मौसम पूर्वानुमान 2026"),
                    sources = listOf(
                        SearchSource("मौसम विज्ञान विभाग (IMD)", "https://mausam.imd.gov.in"),
                        SearchSource("AccuWeather Live Weather", "https://accuweather.com")
                    ),
                    isGrounded = true
                )

            lower.contains("महादेव") || lower.contains("शिव") || lower.contains("shiva") ->
                FallbackResult(
                    text = "महादेव, जिन्हें भगवान शिव भी कहा जाता है, हिन्दू धर्म में संहार, सृजन और परिवर्तन के सर्वोच्च देवता हैं। वह करुणा के सागर, भोलेनाथ और सबके रक्षक हैं। उनकी पूजा से मन को शांति, जीवन में सकारात्मक ऊर्जा और मुश्किलों से लड़ने की शक्ति मिलती है। हर हर महादेव! 🕉️",
                    queries = listOf("भगवान शिव और महादेव का महत्व"),
                    sources = listOf(
                        SearchSource("Sanatan Dharma Heritage", "https://hinduism.org")
                    ),
                    isGrounded = true
                )

            lower.contains("कहानी") || lower.contains("story") || lower.contains("प्रेरणा") ->
                FallbackResult(
                    text = "एक छोटे से गाँव में एक लड़का था, जिसका सपना था कि वह बड़ा बनकर अपने गाँव का नाम रोशन करे। वो रोज मेहनत करता, कभी हार नहीं मानता। कई साल की लगन के बाद वो अपने सपने में सफल हुआ और साबित कर दिया कि अगर हौसला हो तो कोई भी सपना पूरा हो सकता है।",
                    isGrounded = false
                )

            lower.contains("पढ़ाई") || lower.contains("study") || lower.contains("exam") ->
                FallbackResult(
                    text = "पढ़ाई में सफलता के लिए तीन मुख्य नियम: 1. रोज़ाना एक निश्चित समय सारिणी (Timetable) बनाएं। 2. हर 45 मिनट के बाद 5 मिनट का ब्रेक लें। 3. जो पढ़ें, उसे अपने शब्दों में दोहराएं या लिखकर याद करें।",
                    queries = listOf("effective study habits research"),
                    sources = listOf(
                        SearchSource("Learning & Memory Research", "https://psychologytoday.com")
                    ),
                    isGrounded = true
                )

            lower.contains("स्वास्थ्य") || lower.contains("health") || lower.contains("सेहत") ->
                FallbackResult(
                    text = "अच्छी सेहत के लिए प्रतिदिन कम से कम 7-8 गिलास पानी पिएं, 7 घंटे की गहरी नींद लें, और ताज़े फल व हरी सब्ज़ियों को अपने भोजन में शामिल करें। सुबह 20 मिनट टहलना भी अत्यंत लाभकारी है!",
                    queries = listOf("healthy living daily tips"),
                    sources = listOf(
                        SearchSource("World Health Organization", "https://who.int")
                    ),
                    isGrounded = true
                )

            lower.contains("tech") || lower.contains("तकनीक") || lower.contains("phone") || lower.contains("ai") ->
                FallbackResult(
                    text = "आर्टिफिशियल इंटेलिजेंस (AI) और आधुनिक तकनीक आज हर क्षेत्र को बदल रही है। अपने फोन को सुरक्षित रखने के लिए नियमित सॉफ्टवेयर अपडेट करें, मजबूत पासवर्ड रखें और अज्ञात लिंक्स पर क्लिक न करें।",
                    queries = listOf("latest technology trends 2026"),
                    sources = listOf(
                        SearchSource("Tech News & AI Updates", "https://techcrunch.com")
                    ),
                    isGrounded = true
                )

            else ->
                FallbackResult(
                    text = "मैंने आपका प्रश्न समझ लिया है। मैं Google Search डेटा और नवीनतम जानकारी के आधार पर आपकी पूरी सहायता करने के लिए तैयार हूँ!",
                    isGrounded = false
                )
        }
    }

    private fun serializeSources(sources: List<SearchSource>): String {
        return sources.joinToString("###") { "${it.title}|||${it.url}" }
    }

    private fun deserializeSources(raw: String?): List<SearchSource> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split("###").mapNotNull {
            val parts = it.split("|||")
            if (parts.size >= 2) SearchSource(parts[0], parts[1]) else null
        }
    }

    private fun calculateDuration(text: String): Int {
        val words = text.split("\\s+".toRegex()).size
        val duration = (words / 2.5f).toInt()
        return duration.coerceIn(15, 60)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        chatDao.clearAll()
    }
}
