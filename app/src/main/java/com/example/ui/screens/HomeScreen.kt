package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.data.repository.ProfileRepository
import com.example.ui.components.RobotAvatar
import com.example.ui.theme.MyraBlue
import com.example.ui.theme.MyraCardBg
import com.example.ui.theme.MyraCardBorder
import com.example.ui.theme.MyraCyan
import com.example.ui.theme.MyraDarkBg
import com.example.ui.theme.MyraGold
import com.example.ui.theme.MyraNeonBlue
import com.example.ui.theme.MyraPurple
import com.example.ui.theme.MyraPurpleLight
import com.example.ui.theme.MyraTextHint
import com.example.ui.theme.MyraTextPrimary
import com.example.ui.theme.MyraTextSecondary

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    onVoiceClick: () -> Unit,
    onSampleQuestionClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val currentTheme = ProfileRepository.THEME_OPTIONS.find { it.id == userProfile.backgroundThemeId }
        ?: ProfileRepository.THEME_OPTIONS.first()

    val currentAvatar = ProfileRepository.AVATAR_OPTIONS.find { it.id == userProfile.avatarId }
        ?: ProfileRepository.AVATAR_OPTIONS.first()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_mic")
    val micPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    val quickQuestions = listOf(
        "आज का मौसम कैसा है? 🌦️",
        "महादेव के बारे में बताओ 🕉️",
        "प्रेरणादायक कहानी सुनाओ 📖",
        "पढ़ाई के लिए 5 टिप्स 🎓",
        "स्वास्थ्य के लिए सुझाव 💓"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = currentTheme.gradientColors.map { Color(it) }
                )
            )
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Profile quick badge
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MyraCardBg.copy(alpha = 0.8f)),
            modifier = Modifier
                .border(1.dp, MyraCardBorder, RoundedCornerShape(20.dp))
                .clickable { onProfileClick() }
                .testTag("home_profile_badge")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = currentAvatar.emoji, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${userProfile.userName} • टोन: ${userProfile.aiTone}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MyraCyan
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "✏️", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Center Hero Robot Avatar
        RobotAvatar(
            size = 130.dp,
            showGlow = true,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hindi Welcome Heading personalized with user's name
        Text(
            text = "नमस्कार ${userProfile.userName} जी!",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = MyraTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "मैं हूँ MYRA AI",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MyraCyan,
            textAlign = TextAlign.Center
        )

        Text(
            text = "आपका व्यक्तिगत स्मार्ट असिस्टेंट",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MyraTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "आप मुझसे कुछ भी पूछ सकते हैं...",
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = MyraTextHint,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Big Mic Action Button ("बोलकर पूछें")
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(110.dp)
                    .clickable { onVoiceClick() }
                    .testTag("voice_listen_big_button")
            ) {
                // Animated pulse outer ring
                Box(
                    modifier = Modifier
                        .size(105.dp)
                        .scale(micPulse)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MyraCyan.copy(alpha = 0.4f),
                                    MyraBlue.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Outer border glow
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(MyraCyan, MyraBlue)
                            )
                        )
                        .border(3.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                        .shadow(12.dp, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Speak Mic",
                        tint = Color.White,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "बोलकर पूछें",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MyraCyan,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Quick Suggestion Chips
        Text(
            text = "तुरंत पूछें (Quick Prompts):",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MyraTextSecondary,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(quickQuestions) { question ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MyraCardBg
                    ),
                    modifier = Modifier
                        .border(1.dp, MyraCardBorder, RoundedCornerShape(20.dp))
                        .clickable { onSampleQuestionClick(question.replace(" [^\\w\\s]".toRegex(), "").trim()) }
                ) {
                    Text(
                        text = question,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MyraTextPrimary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
