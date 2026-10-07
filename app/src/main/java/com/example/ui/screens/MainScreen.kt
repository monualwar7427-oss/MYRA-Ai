package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ProfileRepository
import com.example.ui.components.TipBanner
import com.example.ui.theme.MyraBlue
import com.example.ui.theme.MyraCardBg
import com.example.ui.theme.MyraCyan
import com.example.ui.theme.MyraDarkBg
import com.example.ui.theme.MyraGold
import com.example.ui.theme.MyraTextHint
import com.example.ui.theme.MyraTextPrimary
import com.example.ui.theme.MyraTextSecondary
import com.example.ui.viewmodel.ChatViewModel

@Composable
fun MainScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val speechRms by viewModel.speechRms.collectAsState()
    val speechPartialText by viewModel.speechPartialText.collectAsState()
    val currentPlaybackSeconds by viewModel.currentPlaybackSeconds.collectAsState()
    val currentTipStep by viewModel.currentTipStep.collectAsState()

    val sessionState by viewModel.voiceSessionState.collectAsState()
    val voiceLanguage by viewModel.voiceLanguage.collectAsState()
    val isHandsFreeLoop by viewModel.isHandsFreeLoop.collectAsState()
    val latestVoiceQuestion by viewModel.latestVoiceQuestion.collectAsState()
    val latestVoiceAnswer by viewModel.latestVoiceAnswer.collectAsState()

    var showProDialog by remember { mutableStateOf(false) }

    // Navigation back press handler
    BackHandler(enabled = selectedTab != 0) {
        viewModel.setSelectedTab(0)
    }

    val stepTips = mapOf(
        1 to "1. ऐप खोलें और माइक्रोफोन पर क्लिक करें या बोलना शुरू करें।",
        2 to "2. अब अपना सवाल बोलें... जैसे: 'आज का मौसम कैसा है?'",
        3 to "3. AI आपका जवाब देगा और आप चाहें तो उसे सुन भी सकते हैं।",
        4 to "4. आप किसी भी विषय पर सवाल पूछ सकते हैं और AI आपको विस्तार से जवाब देगा।",
        5 to "5. AI की आवाज में जवाब सुनें (आप चाहें तो टेक्स्ट भी पढ़ सकते हैं)।",
        6 to "6. अब आप अलग-अलग विषयों पर बोलकर सवाल पूछ सकते हैं और जवाब सुन सकते हैं।"
    )

    val currentAvatar = ProfileRepository.AVATAR_OPTIONS.find { it.id == userProfile.avatarId }
        ?: ProfileRepository.AVATAR_OPTIONS.first()

    Scaffold(
        topBar = {
            if (selectedTab == 0) {
                // Top App Bar for Home Tab matching Screenshot 1
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .background(Color(0xFF070D1E))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.setSelectedTab(4) }, // Open categories
                        modifier = Modifier.testTag("home_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "MYRA AI",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Your Smart AI Assistant",
                            fontSize = 11.sp,
                            color = MyraCyan
                        )
                    }

                    // User Profile Avatar Chip Button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color(currentAvatar.hexColor).copy(alpha = 0.3f))
                            .border(1.5.dp, Color(currentAvatar.hexColor), androidx.compose.foundation.shape.CircleShape)
                            .clickable { viewModel.setSelectedTab(5) }
                            .testTag("home_profile_avatar_button")
                    ) {
                        Text(text = currentAvatar.emoji, fontSize = 18.sp)
                    }

                    // Gold Crown Pro Icon matching Screenshot 1
                    IconButton(
                        onClick = { showProDialog = true },
                        modifier = Modifier.testTag("home_crown_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Pro Assistant",
                            tint = MyraGold,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                // Interactive Tip Banner matching all 6 screenshots
                val tipText = stepTips[currentTipStep] ?: stepTips[1]!!
                TipBanner(
                    stepNumber = currentTipStep,
                    text = tipText,
                    onNextTip = {
                        val next = if (currentTipStep >= 6) 1 else currentTipStep + 1
                        viewModel.setTipStep(next)
                    }
                )

                // Bottom Tab Navigation Bar matching Screenshot 1:
                // [Chat, Voice, Image, More]
                NavigationBar(
                    containerColor = Color(0xFF070D1E),
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    // Chat tab (Tab 0)
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = {
                            viewModel.setSelectedTab(0)
                            viewModel.setTipStep(1)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "Chat",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("Home", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MyraCyan,
                            selectedTextColor = MyraCyan,
                            unselectedIconColor = MyraTextHint,
                            unselectedTextColor = MyraTextHint,
                            indicatorColor = Color(0xFF132247)
                        )
                    )

                    // Voice tab (Tab 1)
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = {
                            viewModel.setSelectedTab(1)
                            viewModel.setTipStep(2)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("Voice", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MyraCyan,
                            selectedTextColor = MyraCyan,
                            unselectedIconColor = MyraTextHint,
                            unselectedTextColor = MyraTextHint,
                            indicatorColor = Color(0xFF132247)
                        )
                    )

                    // Chat History & Q&A tab (Tab 2)
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = {
                            viewModel.setSelectedTab(2)
                            viewModel.setTipStep(3)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "Messages",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("Chat", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MyraCyan,
                            selectedTextColor = MyraCyan,
                            unselectedIconColor = MyraTextHint,
                            unselectedTextColor = MyraTextHint,
                            indicatorColor = Color(0xFF132247)
                        )
                    )

                    // Image tab (Tab 3)
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = {
                            viewModel.setSelectedTab(3)
                            viewModel.setTipStep(4)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Image",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("Image", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MyraCyan,
                            selectedTextColor = MyraCyan,
                            unselectedIconColor = MyraTextHint,
                            unselectedTextColor = MyraTextHint,
                            indicatorColor = Color(0xFF132247)
                        )
                    )

                    // More tab (Tab 4)
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = {
                            viewModel.setSelectedTab(4)
                            viewModel.setTipStep(6)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = "More",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("More", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MyraCyan,
                            selectedTextColor = MyraCyan,
                            unselectedIconColor = MyraTextHint,
                            unselectedTextColor = MyraTextHint,
                            indicatorColor = Color(0xFF132247)
                        )
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> {
                    HomeScreen(
                        userProfile = userProfile,
                        onVoiceClick = {
                            viewModel.setSelectedTab(1)
                            viewModel.setTipStep(2)
                        },
                        onSampleQuestionClick = { query ->
                            viewModel.sendMessage(query)
                            viewModel.setSelectedTab(2)
                            viewModel.setTipStep(3)
                        },
                        onProfileClick = { viewModel.setSelectedTab(5) }
                    )
                }

                1 -> {
                    VoiceListeningScreen(
                        sessionState = sessionState,
                        voiceLanguage = voiceLanguage,
                        isHandsFreeLoop = isHandsFreeLoop,
                        amplitude = speechRms,
                        partialText = speechPartialText,
                        latestUserQuestion = latestVoiceQuestion,
                        latestAiAnswer = latestVoiceAnswer,
                        onStartListening = { viewModel.startListening() },
                        onStopListening = { viewModel.stopListening() },
                        onToggleVoiceAction = { viewModel.toggleVoiceAction() },
                        onLanguageChange = { code -> viewModel.setVoiceLanguage(code) },
                        onHandsFreeToggle = { enabled -> viewModel.setHandsFreeLoop(enabled) },
                        onVoiceQuerySelected = { query ->
                            viewModel.sendMessage(query)
                        },
                        onViewChat = {
                            viewModel.setSelectedTab(2)
                            viewModel.setTipStep(3)
                        },
                        onBack = { viewModel.setSelectedTab(0) }
                    )
                }

                2 -> {
                    ChatScreen(
                        messages = messages,
                        isLoading = isLoading,
                        inputText = inputText,
                        currentPlaybackSeconds = currentPlaybackSeconds,
                        userProfile = userProfile,
                        onInputTextChanged = { viewModel.onInputTextChanged(it) },
                        onSendMessage = { query ->
                            viewModel.sendMessage(query)
                        },
                        onVoiceClick = {
                            viewModel.setSelectedTab(1)
                            viewModel.setTipStep(2)
                        },
                        onToggleAudio = { id, text, duration ->
                            viewModel.toggleAudioPlayback(id, text, duration)
                        },
                        onClearChat = { viewModel.clearChat() },
                        onOpenProfile = { viewModel.setSelectedTab(5) },
                        onBack = { viewModel.setSelectedTab(0) }
                    )
                }

                3 -> {
                    ImageStudioScreen(
                        onPromptSelect = { prompt ->
                            viewModel.sendMessage(prompt, "Image")
                            viewModel.setSelectedTab(2)
                            viewModel.setTipStep(3)
                        },
                        onBack = { viewModel.setSelectedTab(0) }
                    )
                }

                4 -> {
                    CategoriesScreen(
                        categories = viewModel.categories,
                        userProfile = userProfile,
                        onCategoryClick = { category ->
                            category.samplePrompts.firstOrNull()?.let { prompt ->
                                viewModel.sendMessage(prompt, category.title)
                                viewModel.setSelectedTab(2)
                                viewModel.setTipStep(4)
                            }
                        },
                        onVoiceClick = {
                            viewModel.setSelectedTab(1)
                            viewModel.setTipStep(2)
                        },
                        onProfileClick = { viewModel.setSelectedTab(5) },
                        onSettingsClick = { showProDialog = true },
                        onBack = { viewModel.setSelectedTab(0) }
                    )
                }

                5 -> {
                    ProfileScreen(
                        currentProfile = userProfile,
                        onSaveProfile = { updated ->
                            viewModel.updateUserProfile(updated)
                        },
                        onTestVoice = { sample, speed, pitch, lang ->
                            viewModel.testVoice(sample, speed, pitch, lang)
                        },
                        onBack = { viewModel.setSelectedTab(0) }
                    )
                }
            }
        }
    }

    if (showProDialog) {
        AlertDialog(
            onDismissRequest = { showProDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MyraGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "MYRA AI Pro",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "🌟 स्मार्ट वॉयस और एआई फीचर्स:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MyraCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Gemini 3.5 Flash पावर्ड सुपर-फास्ट उत्तर\n• हिंदी व अंग्रेजी में रियल-टाइम वॉयस टॉक\n• सभी श्रेणियों के लिए अनुकूलित ज्ञान भंडार\n• ऑफलाइन स्मार्ट फॉलबैक सपोर्ट",
                        fontSize = 13.sp,
                        color = MyraTextSecondary,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showProDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MyraBlue)
                ) {
                    Text("ठीक है (OK)", color = Color.White)
                }
            },
            containerColor = Color(0xFF0F1B38),
            shape = RoundedCornerShape(20.dp)
        )
    }
}
