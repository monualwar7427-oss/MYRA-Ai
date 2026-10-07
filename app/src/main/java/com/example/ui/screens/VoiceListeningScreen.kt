package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.AudioWaveVisualizer
import com.example.ui.components.RobotAvatar
import com.example.ui.theme.MyraBlue
import com.example.ui.theme.MyraCardBg
import com.example.ui.theme.MyraCardBorder
import com.example.ui.theme.MyraCyan
import com.example.ui.theme.MyraDarkBg
import com.example.ui.theme.MyraPink
import com.example.ui.theme.MyraPurple
import com.example.ui.theme.MyraPurpleLight
import com.example.ui.theme.MyraTextHint
import com.example.ui.theme.MyraTextPrimary
import com.example.ui.theme.MyraTextSecondary
import com.example.ui.viewmodel.VoiceSessionState

@Composable
fun VoiceListeningScreen(
    sessionState: VoiceSessionState,
    voiceLanguage: String,
    isHandsFreeLoop: Boolean,
    amplitude: Float,
    partialText: String,
    latestUserQuestion: String,
    latestAiAnswer: String,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onToggleVoiceAction: () -> Unit,
    onLanguageChange: (String) -> Unit,
    onHandsFreeToggle: (Boolean) -> Unit,
    onVoiceQuerySelected: (String) -> Unit,
    onViewChat: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onStartListening()
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            onStartListening()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "voice_orb_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_scale"
    )

    val sampleVoiceQueries = listOf(
        "आज का मौसम कैसा है? ☀️",
        "Who is Mahadev? 🕉️",
        "मेरे लिए एक प्रेरणादायक कहानी सुनाओ 📖",
        "Tell me a short motivational quote in English ✨",
        "पढ़ाई में ध्यान लगाने के टिप्स 🎓"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF040816),
                        Color(0xFF0B142F),
                        Color(0xFF020614)
                    )
                )
            )
            .padding(14.dp)
    ) {
        // Top Navigation Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    onStopListening()
                    onBack()
                },
                modifier = Modifier.testTag("voice_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "MYRA Live Voice",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "रियल-टाइम वॉयस बातचीत (Hindi & English)",
                    fontSize = 11.sp,
                    color = MyraCyan
                )
            }

            IconButton(
                onClick = onViewChat,
                modifier = Modifier.testTag("voice_view_chat_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "View Chat",
                    tint = MyraCyan
                )
            }
        }

        // Language Selector Chips (Hindi / English / Auto)
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            listOf(
                "hi-IN" to "🇮🇳 हिन्दी",
                "en-US" to "🇬🇧 English",
                "auto" to "🌐 Auto"
            ).forEach { (code, label) ->
                val isSelected = voiceLanguage == code
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) MyraBlue else Color(0xFF131D38))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) MyraCyan else MyraCardBorder,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onLanguageChange(code) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("voice_lang_chip_$code")
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else MyraTextSecondary
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Central Robot Avatar with Dynamic Speaking / Listening Aura
            val avatarGlow = sessionState == VoiceSessionState.LISTENING || sessionState == VoiceSessionState.SPEAKING
            RobotAvatar(
                size = 100.dp,
                showGlow = avatarGlow,
                modifier = Modifier.scale(if (sessionState == VoiceSessionState.SPEAKING) pulseScale else 1f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic Status Badge
            val statusColor = when (sessionState) {
                VoiceSessionState.LISTENING -> MyraCyan
                VoiceSessionState.THINKING -> MyraPink
                VoiceSessionState.SPEAKING -> MyraPurpleLight
                VoiceSessionState.IDLE -> MyraTextSecondary
            }

            val statusText = when (sessionState) {
                VoiceSessionState.LISTENING -> "सुन रहा हूँ... बोलिए (Listening...)"
                VoiceSessionState.THINKING -> "मायरा सोच रही है... (Thinking...)"
                VoiceSessionState.SPEAKING -> "मायरा बोल रही है... (Speaking...)"
                VoiceSessionState.IDLE -> "बात करने के लिए टैप करें (Tap to Speak)"
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.18f)),
                modifier = Modifier.border(1.dp, statusColor.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Live Transcript Box
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F182E).copy(alpha = 0.85f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .border(1.dp, MyraCardBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // What user is saying
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = MyraCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "आप (You):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MyraCyan
                        )
                    }

                    val userDisplayText = when {
                        partialText.isNotEmpty() -> partialText
                        latestUserQuestion.isNotEmpty() -> latestUserQuestion
                        else -> "मायरा से हिंदी या English में कुछ भी पूछें..."
                    }

                    Text(
                        text = userDisplayText,
                        fontSize = 14.sp,
                        color = if (partialText.isNotEmpty() || latestUserQuestion.isNotEmpty()) Color.White else MyraTextHint,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )

                    // Latest MYRA response preview (if available)
                    if (latestAiAnswer.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = MyraPurpleLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MYRA:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MyraPurpleLight
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "🌐 Google Search Grounded",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MyraCyan
                            )
                        }

                        Text(
                            text = latestAiAnswer,
                            fontSize = 13.sp,
                            color = Color(0xFFE2E8F0),
                            maxLines = 4,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dynamic Audio Wave Visualizer
            val isWaveActive = sessionState == VoiceSessionState.LISTENING || sessionState == VoiceSessionState.SPEAKING
            AudioWaveVisualizer(
                isListening = isWaveActive,
                amplitude = amplitude,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Hands-Free Loop Toggle Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111D3B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .border(1.dp, MyraCardBorder, RoundedCornerShape(14.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = MyraCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "हैंड्स-फ्री बातचीत (Continuous Loop)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = "MYRA के उत्तर के बाद स्वतः सुनना शुरू करें",
                                fontSize = 10.sp,
                                color = MyraTextSecondary
                            )
                        }
                    }

                    Switch(
                        checked = isHandsFreeLoop,
                        onCheckedChange = onHandsFreeToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MyraCyan,
                            uncheckedTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("hands_free_loop_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Clickable Simulated Voice Queries
            Text(
                text = "क्विक वॉयस टेस्ट सवाल (Quick Prompts):",
                fontSize = 12.sp,
                color = MyraTextSecondary,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                items(sampleVoiceQueries) { query ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MyraCardBg),
                        modifier = Modifier
                            .border(1.dp, MyraCardBorder, RoundedCornerShape(14.dp))
                            .clickable {
                                onStopListening()
                                onVoiceQuerySelected(query)
                            }
                            .testTag("voice_quick_query")
                    ) {
                        Text(
                            text = query,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MyraCyan,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Central Microphone Action Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = when (sessionState) {
                                VoiceSessionState.LISTENING -> listOf(MyraCyan, MyraBlue)
                                VoiceSessionState.SPEAKING -> listOf(MyraPurple, MyraPink)
                                VoiceSessionState.THINKING -> listOf(MyraPink, MyraBlue)
                                VoiceSessionState.IDLE -> listOf(MyraBlue, MyraCyan)
                            }
                        )
                    )
                    .border(2.5.dp, Color.White.copy(alpha = 0.9f), CircleShape)
                    .clickable { onToggleVoiceAction() }
                    .testTag("voice_main_action_button")
            ) {
                Icon(
                    imageVector = when (sessionState) {
                        VoiceSessionState.LISTENING -> Icons.Default.Close
                        VoiceSessionState.SPEAKING -> Icons.Default.Stop
                        VoiceSessionState.THINKING -> Icons.Default.GraphicEq
                        VoiceSessionState.IDLE -> Icons.Default.Mic
                    },
                    contentDescription = "Voice Action",
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = when (sessionState) {
                    VoiceSessionState.LISTENING -> "रोकने के लिए टैप करें (Stop)"
                    VoiceSessionState.SPEAKING -> "रोकें / इंटरप्ट करें (Interrupt)"
                    VoiceSessionState.THINKING -> "उत्तर तैयार हो रहा है..."
                    VoiceSessionState.IDLE -> "बोलना शुरू करें (Tap to Talk)"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MyraTextSecondary
            )
        }
    }
}
