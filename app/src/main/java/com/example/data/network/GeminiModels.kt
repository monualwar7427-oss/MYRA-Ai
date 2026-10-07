package com.example.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @field:Json(name = "contents") val contents: List<Content>,
    @field:Json(name = "systemInstruction") val systemInstruction: Content? = null,
    @field:Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @field:Json(name = "tools") val tools: List<Tool>? = null
)

@JsonClass(generateAdapter = true)
data class Tool(
    @field:Json(name = "googleSearch") val googleSearch: GoogleSearchTool? = null
)

@JsonClass(generateAdapter = true)
class GoogleSearchTool

@JsonClass(generateAdapter = true)
data class Content(
    @field:Json(name = "parts") val parts: List<Part>,
    @field:Json(name = "role") val role: String? = null
)

@JsonClass(generateAdapter = true)
data class Part(
    @field:Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @field:Json(name = "temperature") val temperature: Float = 0.7f,
    @field:Json(name = "topP") val topP: Float = 0.95f,
    @field:Json(name = "maxOutputTokens") val maxOutputTokens: Int = 1024
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @field:Json(name = "candidates") val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @field:Json(name = "content") val content: Content? = null,
    @field:Json(name = "groundingMetadata") val groundingMetadata: GroundingMetadata? = null
)

@JsonClass(generateAdapter = true)
data class GroundingMetadata(
    @field:Json(name = "webSearchQueries") val webSearchQueries: List<String>? = null,
    @field:Json(name = "groundingChunks") val groundingChunks: List<GroundingChunk>? = null
)

@JsonClass(generateAdapter = true)
data class GroundingChunk(
    @field:Json(name = "web") val web: WebChunk? = null
)

@JsonClass(generateAdapter = true)
data class WebChunk(
    @field:Json(name = "uri") val uri: String? = null,
    @field:Json(name = "title") val title: String? = null
)
