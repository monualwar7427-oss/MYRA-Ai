package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.data.repository.ProfileRepository
import com.example.ui.theme.MyraBlue
import com.example.ui.theme.MyraCardBg
import com.example.ui.theme.MyraCardBorder
import com.example.ui.theme.MyraCyan
import com.example.ui.theme.MyraDarkBg
import com.example.ui.theme.MyraGold
import com.example.ui.theme.MyraPurple
import com.example.ui.theme.MyraPurpleLight
import com.example.ui.theme.MyraTextHint
import com.example.ui.theme.MyraTextPrimary
import com.example.ui.theme.MyraTextSecondary

@Composable
fun ProfileScreen(
    currentProfile: UserProfile,
    onSaveProfile: (UserProfile) -> Unit,
    onTestVoice: (String, Float, Float, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var userName by remember { mutableStateOf(currentProfile.userName) }
    var userBio by remember { mutableStateOf(currentProfile.userBio) }
    var selectedAvatarId by remember { mutableStateOf(currentProfile.avatarId) }
    var selectedThemeId by remember { mutableStateOf(currentProfile.backgroundThemeId) }
    var selectedTone by remember { mutableStateOf(currentProfile.aiTone) }
    var selectedLanguage by remember { mutableStateOf(currentProfile.languageMode) }
    var voiceSpeed by remember { mutableFloatStateOf(currentProfile.voiceSpeed) }
    var voicePitch by remember { mutableFloatStateOf(currentProfile.voicePitch) }
    var autoSpeak by remember { mutableStateOf(currentProfile.autoSpeakResponse) }
    var customInstructions by remember { mutableStateOf(currentProfile.customInstructions) }

    var isSavedToastShown by remember { mutableStateOf(false) }

    val activeAvatar = ProfileRepository.AVATAR_OPTIONS.find { it.id == selectedAvatarId }
        ?: ProfileRepository.AVATAR_OPTIONS.first()

    val activeTheme = ProfileRepository.THEME_OPTIONS.find { it.id == selectedThemeId }
        ?: ProfileRepository.THEME_OPTIONS.first()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = activeTheme.gradientColors.map { Color(it) }
                )
            )
    ) {
        // Top App Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF091024).copy(alpha = 0.85f))
                .padding(horizontal = 8.dp, vertical = 10.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("profile_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "मेरी प्रोफ़ाइल (My Profile)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "अवतार, पृष्ठभूमि व AI प्रतिक्रिया अनुकूलन",
                    fontSize = 11.sp,
                    color = MyraCyan
                )
            }

            IconButton(
                onClick = {
                    val updated = UserProfile(
                        userName = userName.trim().ifEmpty { "यूज़र" },
                        userBio = userBio.trim(),
                        avatarId = selectedAvatarId,
                        backgroundThemeId = selectedThemeId,
                        aiTone = selectedTone,
                        languageMode = selectedLanguage,
                        voiceSpeed = voiceSpeed,
                        voicePitch = voicePitch,
                        autoSpeakResponse = autoSpeak,
                        customInstructions = customInstructions.trim()
                    )
                    onSaveProfile(updated)
                    isSavedToastShown = true
                    Toast.makeText(context, "प्रोफ़ाइल सफलतापूर्वक सुरक्षित हो गई! ✨", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.testTag("profile_save_icon_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save Profile",
                    tint = MyraCyan
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // ----------------------------------------------------
            // 1. Profile Header & Avatar Card
            // ----------------------------------------------------
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MyraCardBg.copy(alpha = 0.9f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MyraCardBorder, RoundedCornerShape(20.dp))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Avatar Circle with Glow
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(Color(activeAvatar.hexColor).copy(alpha = 0.25f))
                                .border(3.dp, Color(activeAvatar.hexColor), CircleShape)
                        ) {
                            Text(
                                text = activeAvatar.emoji,
                                fontSize = 48.sp
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(MyraBlue)
                                .border(2.dp, Color.White, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // User Name Field
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("आपका नाम (Your Name)") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF14213D),
                            unfocusedContainerColor = Color(0xFF0F172A),
                            focusedIndicatorColor = MyraCyan,
                            unfocusedIndicatorColor = MyraCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = MyraCyan,
                            unfocusedLabelColor = MyraTextHint
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bio Field
                    OutlinedTextField(
                        value = userBio,
                        onValueChange = { userBio = it },
                        label = { Text("बायो / स्थिति (Status/Bio)") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF14213D),
                            unfocusedContainerColor = Color(0xFF0F172A),
                            focusedIndicatorColor = MyraCyan,
                            unfocusedIndicatorColor = MyraCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = MyraCyan,
                            unfocusedLabelColor = MyraTextHint
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_bio_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Avatar Selection List
                    Text(
                        text = "अपना पसंदीदा अवतार चुनें:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MyraTextSecondary,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(ProfileRepository.AVATAR_OPTIONS) { item ->
                            val isSelected = item.id == selectedAvatarId
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSelected) MyraCyan.copy(alpha = 0.2f) else Color(0xFF131D38)
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MyraCyan else MyraCardBorder,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedAvatarId = item.id }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                                    .testTag("avatar_option_${item.id}")
                            ) {
                                Text(text = item.emoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.name,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MyraTextHint
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ----------------------------------------------------
            // 2. Background Theme Selection Card
            // ----------------------------------------------------
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MyraCardBg.copy(alpha = 0.9f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MyraCardBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = null,
                            tint = MyraCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "पृष्ठभूमि थीम (Atmospheric Theme)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(ProfileRepository.THEME_OPTIONS) { theme ->
                            val isSelected = theme.id == selectedThemeId
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                modifier = Modifier
                                    .width(115.dp)
                                    .height(75.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = theme.gradientColors.map { Color(it) }
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) MyraCyan else Color.White.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedThemeId = theme.id }
                                    .testTag("theme_option_${theme.id}")
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = theme.hindiName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = theme.name,
                                        fontSize = 9.sp,
                                        color = MyraCyan
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ----------------------------------------------------
            // 3. AI Response Customization (AI की प्रतिक्रिया अनुकूलन)
            // ----------------------------------------------------
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MyraCardBg.copy(alpha = 0.9f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MyraCardBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = MyraPurpleLight,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI प्रतिक्रिया अनुकूलन (Response Persona)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "AI का स्वभाव व टोन (Tone of MYRA):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MyraTextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ProfileRepository.AI_TONE_OPTIONS.forEach { (tone, desc) ->
                        val isSelected = tone == selectedTone
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MyraPurple.copy(alpha = 0.25f) else Color(0xFF111C36)
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MyraCyan else MyraCardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedTone = tone }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("ai_tone_$tone")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MyraCyan else Color.Transparent)
                                    .border(1.5.dp, if (isSelected) MyraCyan else MyraTextHint, CircleShape)
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF091024),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = tone,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = desc,
                                    fontSize = 11.sp,
                                    color = MyraTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "प्राथमिक भाषा शैली (Language Preference):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MyraTextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ProfileRepository.LANGUAGE_OPTIONS.forEach { (lang, _) ->
                            val isSelected = lang == selectedLanguage
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MyraBlue else Color(0xFF111C36)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) MyraCyan else MyraCardBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedLanguage = lang }
                                    .padding(vertical = 10.dp)
                                    .testTag("ai_lang_$lang")
                            ) {
                                Text(
                                    text = lang,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Custom Instructions for AI
                    Text(
                        text = "कस्टम निर्देश (Special Instructions to MYRA):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MyraTextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = customInstructions,
                        onValueChange = { customInstructions = it },
                        placeholder = {
                            Text(
                                "जैसे: मुझे 'मित्र' कहकर पुकारें, हमेशा उदाहरण देकर समझाएं...",
                                fontSize = 12.sp,
                                color = MyraTextHint
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF14213D),
                            unfocusedContainerColor = Color(0xFF0F172A),
                            focusedIndicatorColor = MyraCyan,
                            unfocusedIndicatorColor = MyraCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_custom_instructions_input")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ----------------------------------------------------
            // 4. Voice & Speech Synthesis Settings
            // ----------------------------------------------------
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MyraCardBg.copy(alpha = 0.9f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MyraCardBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = MyraCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "वॉयस व स्पीच सेटिंग्स (Voice & Audio)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Auto speak toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ऑटो वॉयस उत्तर (Auto Read Answers)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = "हर उत्तर को MYRA अपने-आप बोलकर सुनाए",
                                fontSize = 11.sp,
                                color = MyraTextSecondary
                            )
                        }

                        Switch(
                            checked = autoSpeak,
                            onCheckedChange = { autoSpeak = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MyraCyan,
                                uncheckedTrackColor = Color(0xFF1E293B)
                            ),
                            modifier = Modifier.testTag("profile_auto_speak_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Voice Speed Slider
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "बोलने की गति (Speech Speed):",
                            fontSize = 13.sp,
                            color = MyraTextSecondary
                        )
                        Text(
                            text = "${String.format("%.2f", voiceSpeed)}x",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MyraCyan
                        )
                    }

                    Slider(
                        value = voiceSpeed,
                        onValueChange = { voiceSpeed = it },
                        valueRange = 0.75f..1.5f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = MyraCyan,
                            activeTrackColor = MyraCyan,
                            inactiveTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("profile_voice_speed_slider")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Voice Pitch Slider
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "वॉयस पिच (Voice Pitch):",
                            fontSize = 13.sp,
                            color = MyraTextSecondary
                        )
                        Text(
                            text = "${String.format("%.2f", voicePitch)}x",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MyraPurpleLight
                        )
                    }

                    Slider(
                        value = voicePitch,
                        onValueChange = { voicePitch = it },
                        valueRange = 0.8f..1.3f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = MyraPurpleLight,
                            activeTrackColor = MyraPurpleLight,
                            inactiveTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("profile_voice_pitch_slider")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Test Voice Sample Button
                    Button(
                        onClick = {
                            val sample = "नमस्ते $userName जी! मैं हूँ MYRA AI। आपकी अनुकूलित आवाज ऐसी सुनाई देगी।"
                            onTestVoice(sample, voiceSpeed, voicePitch, selectedLanguage)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF162344)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MyraCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .testTag("profile_test_voice_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = MyraCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "आवाज टेस्ट करें (Hear Voice Preview)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MyraCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Profile Big Button
            Button(
                onClick = {
                    val updated = UserProfile(
                        userName = userName.trim().ifEmpty { "यूज़र" },
                        userBio = userBio.trim(),
                        avatarId = selectedAvatarId,
                        backgroundThemeId = selectedThemeId,
                        aiTone = selectedTone,
                        languageMode = selectedLanguage,
                        voiceSpeed = voiceSpeed,
                        voicePitch = voicePitch,
                        autoSpeakResponse = autoSpeak,
                        customInstructions = customInstructions.trim()
                    )
                    onSaveProfile(updated)
                    Toast.makeText(context, "प्रोफ़ाइल सफलतापूर्वक सुरक्षित हो गई! ✨", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyraBlue
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("profile_save_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "प्रोफ़ाइल सुरक्षित करें (Save Profile)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
