package com.example.ui.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.ChatMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TranscriptExporter {
    fun formatTranscript(
        messages: List<ChatMessage>,
        userName: String
    ): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val sb = StringBuilder()
        sb.appendLine("==================================================")
        sb.appendLine("MYRA AI - Chat Transcript")
        sb.appendLine("Exported on: ${dateFormat.format(Date())}")
        sb.appendLine("User: $userName")
        sb.appendLine("Total Messages: ${messages.size}")
        sb.appendLine("==================================================")
        sb.appendLine()

        for (msg in messages) {
            val sender = if (msg.isUser) userName else "MYRA AI"
            val time = msg.formattedTime.ifEmpty { "N/A" }
            sb.appendLine("[$time] $sender:")
            sb.appendLine(msg.text)

            msg.weatherData?.let { w ->
                sb.appendLine("  [मौसम कार्ड: ${w.condition} | ${w.maxTemp} / ${w.minTemp}]")
            }

            if (msg.searchSources.isNotEmpty()) {
                sb.appendLine("  [वेब स्रोत / Sources:")
                msg.searchSources.forEach { s ->
                    sb.appendLine("    - ${s.title}: ${s.url}")
                }
                sb.appendLine("  ]")
            }
            sb.appendLine("--------------------------------------------------")
            sb.appendLine()
        }

        sb.appendLine("==================================================")
        sb.appendLine("Generated via MYRA AI - Your Smart AI Assistant")
        sb.appendLine("==================================================")
        return sb.toString()
    }

    fun generateFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "MYRA_Chat_Transcript_$timeStamp.txt"
    }

    fun copyTranscript(context: Context, messages: List<ChatMessage>, userName: String): Boolean {
        if (messages.isEmpty()) {
            Toast.makeText(context, "एक्सपोर्ट करने के लिए कोई संदेश नहीं है।", Toast.LENGTH_SHORT).show()
            return false
        }
        val text = formatTranscript(messages, userName)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("MYRA Chat Transcript", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "चैट ट्रांसक्रिप्ट क्लिपबोर्ड पर कॉपी हो गया! 📋", Toast.LENGTH_SHORT).show()
        return true
    }

    fun shareTranscript(context: Context, messages: List<ChatMessage>, userName: String): Boolean {
        if (messages.isEmpty()) {
            Toast.makeText(context, "एक्सपोर्ट करने के लिए कोई संदेश नहीं है।", Toast.LENGTH_SHORT).show()
            return false
        }
        val text = formatTranscript(messages, userName)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "MYRA AI Chat Transcript (${messages.size} Messages)")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val shareIntent = Intent.createChooser(sendIntent, "चैट ट्रांसक्रिप्ट शेयर करें")
        context.startActivity(shareIntent)
        return true
    }

    fun saveTranscriptToUri(context: Context, uri: Uri, messages: List<ChatMessage>, userName: String): Boolean {
        return try {
            val transcriptText = formatTranscript(messages, userName)
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(transcriptText.toByteArray(Charsets.UTF_8))
            }
            Toast.makeText(context, "ट्रांसक्रिप्ट फ़ाइल सफलतापूर्वक सेव हो गई! 📄", Toast.LENGTH_LONG).show()
            true
        } catch (e: Exception) {
            Toast.makeText(context, "सेव करने में त्रुटि: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }
}
