package com.example.data.model

data class CategoryItem(
    val id: String,
    val title: String,
    val hindiTitle: String,
    val iconEmoji: String,
    val samplePrompts: List<String>,
    val description: String = ""
)
