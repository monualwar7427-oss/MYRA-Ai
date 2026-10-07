package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CategoryItem
import com.example.data.model.ChatMessage
import com.example.data.model.UserProfile
import com.example.data.repository.ChatRepository
import com.example.data.repository.ProfileRepository
import com.example.service.SpeechManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class VoiceSessionState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING
}

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ChatRepository(application)
    private val profileRepository = ProfileRepository(application)
    val speechManager = SpeechManager(application)

    val userProfile: StateFlow<UserProfile> = profileRepository.userProfile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Home, 1: Voice, 2: Chat, 3: Image, 4: More, 5: Profile
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _currentTipStep = MutableStateFlow(1)
    val currentTipStep: StateFlow<Int> = _currentTipStep.asStateFlow()

    // Real-time voice conversation state
    val voiceSessionState = MutableStateFlow(VoiceSessionState.IDLE)
    val voiceLanguage = MutableStateFlow("hi-IN") // "hi-IN", "en-US", "auto"
    val isHandsFreeLoop = MutableStateFlow(true)
    val latestVoiceQuestion = MutableStateFlow("")
    val latestVoiceAnswer = MutableStateFlow("")

    val isListening: StateFlow<Boolean> = speechManager.isListening
    val isSpeaking: StateFlow<Boolean> = speechManager.isSpeaking
    val speechRms: StateFlow<Float> = speechManager.speechRms
    val speechPartialText: StateFlow<String> = speechManager.speechPartialText
    val currentPlayingMessageId: StateFlow<Long?> = speechManager.currentPlayingMessageId
    val playbackProgress: StateFlow<Float> = speechManager.playbackProgress
    val currentPlaybackSeconds: StateFlow<Int> = speechManager.currentPlaybackSeconds

    // Combine room messages with active audio playback state
    val messages: StateFlow<List<ChatMessage>> = combine(
        repository.allMessages,
        currentPlayingMessageId,
        playbackProgress
    ) { msgList, activeId, progress ->
        msgList.map { msg ->
            if (msg.id == activeId) {
                msg.copy(isPlaying = true, playbackProgress = progress)
            } else {
                msg.copy(isPlaying = false, playbackProgress = 0f)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: List<CategoryItem> = listOf(
        CategoryItem(
            id = "gk",
            title = "General Knowledge",
            hindiTitle = "सामान्य ज्ञान",
            iconEmoji = "💡",
            samplePrompts = listOf(
                "दुनिया का सबसे ऊँचा पर्वत कौन सा है?",
                "भारत का संविधान कब लागू हुआ था?",
                "सौरमंडल का सबसे बड़ा ग्रह कौन सा है?"
            ),
            description = "इतिहास, विज्ञान और भूगोल के रोचक तथ्य"
        ),
        CategoryItem(
            id = "study",
            title = "Study Help",
            hindiTitle = "पढ़ाई में मदद",
            iconEmoji = "🎓",
            samplePrompts = listOf(
                "गणित के सूत्रों को याद करने की ट्रिक बताओ",
                "परीक्षा की तैयारी कैसे करें?",
                "अंग्रेजी व्याकरण के मुख्य नियम समझाओ"
            ),
            description = "परीक्षा तैयारी और कठिन विषयों की आसान व्याख्या"
        ),
        CategoryItem(
            id = "travel",
            title = "Travel Guide",
            hindiTitle = "यात्रा मार्गदर्शन",
            iconEmoji = "📍",
            samplePrompts = listOf(
                "मनाली घूमने का सबसे अच्छा समय क्या है?",
                "जयपुर में देखने लायक प्रमुख जगहें बताओ",
                "बजट ट्रिप की योजना कैसे बनाएं?"
            ),
            description = "पर्यटन स्थल और यात्रा सुझाव"
        ),
        CategoryItem(
            id = "health",
            title = "Health Tips",
            hindiTitle = "स्वास्थ्य सुझाव",
            iconEmoji = "💓",
            samplePrompts = listOf(
                "रोजाना कितना पानी पीना चाहिए?",
                "वजन घटाने के लिए 5 आसान टिप्स",
                "अच्छी नींद के लिए क्या करें?"
            ),
            description = "योग, पोषण और तंदुरुस्ती के उपाय"
        ),
        CategoryItem(
            id = "tech",
            title = "Tech Support",
            hindiTitle = "टेक सपोर्ट",
            iconEmoji = "⚙️",
            samplePrompts = listOf(
                "फोन की बैटरी लाइफ कैसे बढ़ाएं?",
                "आर्टिफिशियल इंटेलिजेंस क्या है?",
                "पासवर्ड को सुरक्षित रखने के तरीके"
            ),
            description = "गैजेट्स, ऐप्स और तकनीकी समस्याओं का हल"
        ),
        CategoryItem(
            id = "more",
            title = "And More...",
            hindiTitle = "और भी बहुत कुछ",
            iconEmoji = "⋯",
            samplePrompts = listOf(
                "महादेव के बारे में बताओ",
                "मेरे लिए एक प्रेरणादायक कहानी सुनाओ",
                "एक खूबसूरत प्रेरणादायक शायरी सुनाओ"
            ),
            description = "कहानियां, भक्ति, शायरी और दैनिक राशिफल"
        )
    )

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
        }

        // Keep speechManager updated with user's voice speed and pitch preference
        viewModelScope.launch {
            userProfile.collect { profile ->
                speechManager.updateVoiceSettings(profile.voiceSpeed, profile.voicePitch, profile.languageMode)
            }
        }

        // Listen for speech results from recognizer
        viewModelScope.launch {
            speechManager.speechResult.collect { result ->
                if (!result.isNullOrBlank()) {
                    latestVoiceQuestion.value = result
                    voiceSessionState.value = VoiceSessionState.THINKING
                    sendMessage(result)
                }
            }
        }

        // Handle completion of TTS speaking for continuous hands-free voice loop
        speechManager.onTtsFinished = {
            if (_selectedTab.value == 1 && isHandsFreeLoop.value) {
                viewModelScope.launch {
                    delay(500)
                    if (_selectedTab.value == 1 && isHandsFreeLoop.value) {
                        startListening()
                    } else {
                        voiceSessionState.value = VoiceSessionState.IDLE
                    }
                }
            } else {
                voiceSessionState.value = VoiceSessionState.IDLE
            }
        }
    }

    fun setVoiceLanguage(lang: String) {
        voiceLanguage.value = lang
        if (isListening.value) {
            startListening()
        }
    }

    fun setHandsFreeLoop(enabled: Boolean) {
        isHandsFreeLoop.value = enabled
    }

    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
        if (tab != 1) {
            // If leaving voice screen, stop continuous listening
            stopListening()
            speechManager.stopPlayback()
            voiceSessionState.value = VoiceSessionState.IDLE
        }
    }

    fun setTipStep(step: Int) {
        _currentTipStep.value = step
    }

    fun updateUserProfile(profile: UserProfile) {
        profileRepository.saveProfile(profile)
        speechManager.updateVoiceSettings(profile.voiceSpeed, profile.voicePitch, profile.languageMode)
    }

    fun testVoice(sampleText: String, speed: Float, pitch: Float, languageMode: String) {
        speechManager.testSampleVoice(sampleText, speed, pitch, languageMode)
    }

    fun sendMessage(text: String, category: String = "General") {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        _inputText.value = ""
        viewModelScope.launch {
            _isLoading.value = true
            repository.saveUserMessage(trimmed, category)
            val currentProfile = userProfile.value
            val aiMsg = repository.getAiResponse(trimmed, category, currentProfile)
            _isLoading.value = false

            latestVoiceAnswer.value = aiMsg.text

            // If in voice tab, update state to speaking
            if (_selectedTab.value == 1) {
                voiceSessionState.value = VoiceSessionState.SPEAKING
                speechManager.speak(aiMsg.id, aiMsg.text, aiMsg.audioDurationSeconds)
            } else if (currentProfile.autoSpeakResponse) {
                speechManager.speak(aiMsg.id, aiMsg.text, aiMsg.audioDurationSeconds)
            }
        }
    }

    fun startListening() {
        speechManager.stopPlayback()
        voiceSessionState.value = VoiceSessionState.LISTENING
        speechManager.startListening(voiceLanguage.value)
    }

    fun stopListening() {
        speechManager.stopListening()
        voiceSessionState.value = VoiceSessionState.IDLE
    }

    fun toggleVoiceAction() {
        if (isListening.value) {
            stopListening()
        } else if (isSpeaking.value) {
            speechManager.stopPlayback()
            voiceSessionState.value = VoiceSessionState.IDLE
        } else {
            startListening()
        }
    }

    fun toggleAudioPlayback(messageId: Long, text: String, duration: Int) {
        speechManager.speak(messageId, text, duration)
    }

    fun clearChat() {
        viewModelScope.launch {
            speechManager.stopPlayback()
            repository.clearHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.cleanUp()
    }
}
