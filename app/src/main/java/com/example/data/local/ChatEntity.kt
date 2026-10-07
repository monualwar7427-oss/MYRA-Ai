package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long,
    val formattedTime: String,
    val audioDurationSeconds: Int,
    val weatherCondition: String?,
    val weatherMaxTemp: String?,
    val weatherMinTemp: String?,
    val category: String,
    val searchQueriesJoined: String? = null,
    val searchSourcesJson: String? = null,
    val isGrounded: Boolean = false
)
