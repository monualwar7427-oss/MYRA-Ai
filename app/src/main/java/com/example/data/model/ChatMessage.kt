package com.example.data.model

data class SearchSource(
    val title: String,
    val url: String
)

data class WeatherCardData(
    val condition: String, // e.g. "Sunny (हल्का बादल)"
    val maxTemp: String,   // "32°C"
    val minTemp: String,   // "22°C"
    val iconEmoji: String  // "☀️"
)

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val formattedTime: String = "",
    val audioDurationSeconds: Int = 30,
    val isPlaying: Boolean = false,
    val playbackProgress: Float = 0f,
    val weatherData: WeatherCardData? = null,
    val category: String = "General",
    val searchQueries: List<String> = emptyList(),
    val searchSources: List<SearchSource> = emptyList(),
    val isGrounded: Boolean = false
)
