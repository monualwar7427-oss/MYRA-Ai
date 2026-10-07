package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.UserProfile
import com.example.data.repository.ProfileRepository
import com.example.ui.components.EmbeddedAudioPlayer
import com.example.ui.components.RobotAvatar
import com.example.ui.components.WeatherCard
import com.example.ui.theme.MyraBlue
import com.example.ui.theme.MyraCardBg
import com.example.ui.theme.MyraCardBorder
import com.example.ui.theme.MyraCyan
import com.example.ui.theme.MyraDarkBg
import com.example.ui.theme.MyraTextHint
import com.example.ui.theme.MyraTextPrimary
import com.example.ui.theme.MyraTextSecondary
import com.example.ui.theme.MyraUserBubble
import com.example.ui.util.TranscriptExporter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    inputText: String,
    currentPlaybackSeconds: Int,
    userProfile: UserProfile,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onVoiceClick: () -> Unit,
    onToggleAudio: (Long, String, Int) -> Unit,
    onClearChat: () -> Unit,
    onOpenProfile: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var showMenu by remember { mutableStateOf(false) }
    var showQuickPromptsDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        if (uri != null) {
            TranscriptExporter.saveTranscriptToUri(context, uri, messages, userProfile.userName)
        }
    }

    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MyraDarkBg)
    ) {
        // Top App Bar matching screenshot
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A1226))
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("chat_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Small Robot Avatar
            RobotAvatar(
                size = 38.dp,
                showGlow = false,
                modifier = Modifier.padding(end = 8.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "MYRA AI",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (isLoading) "टाइप कर रहा है..." else "ऑनलाइन",
                    fontSize = 11.sp,
                    color = if (isLoading) MyraCyan else Color(0xFF4ADE80)
                )
            }

            // Quick Export Icon Button
            IconButton(
                onClick = { showExportDialog = true },
                modifier = Modifier.testTag("chat_export_button")
            ) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = "एक्सपोर्ट करें (Export)",
                    tint = MyraCyan
                )
            }

            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.testTag("chat_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(MyraCardBg)
                ) {
                    DropdownMenuItem(
                        text = { Text("ट्रांसक्रिप्ट एक्सपोर्ट करें (Export Transcript)", color = Color.White) },
                        leadingIcon = {
                            Icon(Icons.Default.FileDownload, contentDescription = null, tint = MyraCyan)
                        },
                        onClick = {
                            showMenu = false
                            showExportDialog = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("मेरी प्रोफ़ाइल (My Profile)", color = Color.White) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MyraCyan)
                        },
                        onClick = {
                            showMenu = false
                            onOpenProfile()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("चैट साफ करें (Clear Chat)", color = Color.White) },
                        leadingIcon = {
                            Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = MyraCyan)
                        },
                        onClick = {
                            showMenu = false
                            onClearChat()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("बोलकर पूछें (Voice Mode)", color = Color.White) },
                        leadingIcon = {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = MyraCyan)
                        },
                        onClick = {
                            showMenu = false
                            onVoiceClick()
                        }
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(messages, key = { it.id }) { msg ->
                if (msg.isUser) {
                    UserMessageItem(message = msg, userProfile = userProfile)
                } else {
                    AiMessageItem(
                        message = msg,
                        currentPlaybackSeconds = currentPlaybackSeconds,
                        onToggleAudio = onToggleAudio
                    )
                }
            }

            if (isLoading) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 48.dp, top = 4.dp)
                    ) {
                        CircularProgressIndicator(
                            color = MyraCyan,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "MYRA सोच रही है...",
                            fontSize = 13.sp,
                            color = MyraTextSecondary
                        )
                    }
                }
            }
        }

        // Quick prompts bar (horizontal chips above input)
        if (showQuickPromptsDialog) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F1A36))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                listOf(
                    "आज का मौसम कैसा है?",
                    "महादेव के बारे में बताओ",
                    "प्रेरणादायक कहानी सुनाओ"
                ).forEach { prompt ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        modifier = Modifier
                            .clickable {
                                showQuickPromptsDialog = false
                                onSendMessage(prompt)
                            }
                    ) {
                        Text(
                            text = prompt,
                            fontSize = 11.sp,
                            color = MyraCyan,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Bottom Input Bar matching screenshots
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF090F20))
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            // (+) Button
            IconButton(
                onClick = { showQuickPromptsDialog = !showQuickPromptsDialog },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MyraBlue)
                    .testTag("chat_plus_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Quick Prompts",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Text input: "यहाँ टाइप करें..."
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChanged,
                placeholder = {
                    Text(
                        text = "यहाँ टाइप करें...",
                        fontSize = 14.sp,
                        color = MyraTextHint
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputText.isNotBlank()) {
                            keyboardController?.hide()
                            onSendMessage(inputText)
                        }
                    }
                ),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF162344),
                    unfocusedContainerColor = Color(0xFF111C36),
                    focusedIndicatorColor = MyraCyan,
                    unfocusedIndicatorColor = Color(0xFF1F325E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("chat_input_field")
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Send / Voice button
            if (inputText.isNotBlank()) {
                IconButton(
                    onClick = {
                        keyboardController?.hide()
                        onSendMessage(inputText)
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(MyraCyan, MyraBlue)
                            )
                        )
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = onVoiceClick,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(MyraCyan, MyraBlue)
                            )
                        )
                        .testTag("chat_mic_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }

    if (showExportDialog) {
        ExportTranscriptDialog(
            messages = messages,
            userName = userProfile.userName,
            onDismiss = { showExportDialog = false },
            onSaveToFile = { fileName ->
                showExportDialog = false
                createDocumentLauncher.launch(fileName)
            },
            onShare = {
                showExportDialog = false
                TranscriptExporter.shareTranscript(context, messages, userProfile.userName)
            },
            onCopy = {
                TranscriptExporter.copyTranscript(context, messages, userProfile.userName)
            }
        )
    }
}

@Composable
fun ExportTranscriptDialog(
    messages: List<ChatMessage>,
    userName: String,
    onDismiss: () -> Unit,
    onSaveToFile: (String) -> Unit,
    onShare: () -> Unit,
    onCopy: () -> Unit
) {
    val suggestedFileName = remember { TranscriptExporter.generateFileName() }
    val formattedTranscript = remember(messages, userName) {
        TranscriptExporter.formatTranscript(messages, userName)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MyraBlue.copy(alpha = 0.25f))
                        .border(1.5.dp, MyraCyan, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        tint = MyraCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "चैट ट्रांसक्रिप्ट एक्सपोर्ट",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "प्लेन टेक्स्ट (.txt) फ़ाइल में सेव करें",
                        fontSize = 11.sp,
                        color = MyraTextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Info Metadata Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A33)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MyraCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "कुल संदेश:",
                                fontSize = 12.sp,
                                color = MyraTextSecondary
                            )
                            Text(
                                text = "${messages.size} संदेश",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MyraCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "प्रारूप (Format):",
                                fontSize = 12.sp,
                                color = MyraTextSecondary
                            )
                            Text(
                                text = "Plain Text (.txt)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF4ADE80)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "फ़ाइल नाम:",
                                fontSize = 12.sp,
                                color = MyraTextSecondary
                            )
                            Text(
                                text = suggestedFileName,
                                fontSize = 10.5.sp,
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "ट्रांसक्रिप्ट पूर्वावलोकन (Preview):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Scrollable plain-text transcript preview
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF060B17)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B2A4A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = if (messages.isEmpty()) {
                                "कोई संदेश उपलब्ध नहीं है। बातचीत के बाद एक्सपोर्ट करें।"
                            } else {
                                formattedTranscript
                            },
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (messages.isEmpty()) MyraTextHint else MyraTextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Primary Action Button: Save as Plain Text File (.txt)
                Button(
                    onClick = { onSaveToFile(suggestedFileName) },
                    colors = ButtonDefaults.buttonColors(containerColor = MyraBlue),
                    shape = RoundedCornerShape(10.dp),
                    enabled = messages.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_transcript_file_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "फ़ाइल सेव करें (.txt)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Secondary Action Buttons: Share and Copy
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onShare,
                        enabled = messages.isNotEmpty(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_transcript_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = MyraCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "शेयर करें",
                            fontSize = 12.sp,
                            color = MyraCyan
                        )
                    }

                    OutlinedButton(
                        onClick = onCopy,
                        enabled = messages.isNotEmpty(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_transcript_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "कॉपी करें",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("बंद करें (Close)", fontSize = 12.sp, color = Color.White)
            }
        },
        containerColor = Color(0xFF0F1A30),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun UserMessageItem(
    message: ChatMessage,
    userProfile: UserProfile,
    modifier: Modifier = Modifier
) {
    val avatarOption = ProfileRepository.AVATAR_OPTIONS.find { it.id == userProfile.avatarId }
        ?: ProfileRepository.AVATAR_OPTIONS.first()

    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Card(
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MyraUserBubble
                ),
                modifier = Modifier.testTag("user_message_bubble")
            ) {
                Text(
                    text = message.text,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            if (message.formattedTime.isNotEmpty()) {
                Text(
                    text = message.formattedTime,
                    fontSize = 10.sp,
                    color = MyraTextHint,
                    modifier = Modifier.padding(top = 4.dp, end = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Personalized User Avatar Circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(avatarOption.hexColor).copy(alpha = 0.3f))
                .border(1.5.dp, Color(avatarOption.hexColor), CircleShape)
        ) {
            Text(
                text = avatarOption.emoji,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun AiMessageItem(
    message: ChatMessage,
    currentPlaybackSeconds: Int,
    onToggleAudio: (Long, String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top,
        modifier = modifier.fillMaxWidth()
    ) {
        // AI Robot Avatar
        RobotAvatar(
            size = 36.dp,
            showGlow = false,
            modifier = Modifier.padding(end = 8.dp)
        )

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(1f)
        ) {
            Card(
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White // Crisp white surface matching screenshot 3, 4, 5
                ),
                modifier = Modifier.testTag("ai_message_bubble")
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    // Response text
                    Text(
                        text = message.text,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF1E293B), // Dark slate text on white background
                        lineHeight = 22.sp
                    )

                    // Optional weather widget
                    message.weatherData?.let { weather ->
                        WeatherCard(weather = weather)
                    }

                    // Google Search Grounding Badge & Sources (Live Web Knowledge)
                    if (message.isGrounded || message.searchSources.isNotEmpty()) {
                        com.example.ui.components.GoogleSearchGroundingCard(
                            queries = message.searchSources,
                            searchQueries = message.searchQueries
                        )
                    }

                    // Embedded Audio Player Widget (Voice Synthesis)
                    EmbeddedAudioPlayer(
                        isPlaying = message.isPlaying,
                        progress = message.playbackProgress,
                        durationSeconds = message.audioDurationSeconds,
                        currentSeconds = if (message.isPlaying) currentPlaybackSeconds else message.audioDurationSeconds,
                        onPlayPauseClick = {
                            onToggleAudio(message.id, message.text, message.audioDurationSeconds)
                        }
                    )
                }
            }

            if (message.formattedTime.isNotEmpty()) {
                Text(
                    text = message.formattedTime,
                    fontSize = 10.sp,
                    color = MyraTextHint,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }
        }
    }
}
