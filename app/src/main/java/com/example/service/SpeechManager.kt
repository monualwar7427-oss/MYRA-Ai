package com.example.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class SpeechManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    private val scope = CoroutineScope(Dispatchers.Main)
    private var playbackJob: Job? = null
    private var ttsWaveJob: Job? = null

    // State flows for listening
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _speechRms = MutableStateFlow(0f)
    val speechRms: StateFlow<Float> = _speechRms.asStateFlow()

    private val _speechResult = MutableStateFlow<String?>(null)
    val speechResult: StateFlow<String?> = _speechResult.asStateFlow()

    private val _speechPartialText = MutableStateFlow("")
    val speechPartialText: StateFlow<String> = _speechPartialText.asStateFlow()

    // State flows for TTS speaking
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentPlayingMessageId = MutableStateFlow<Long?>(null)
    val currentPlayingMessageId: StateFlow<Long?> = _currentPlayingMessageId.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0f)
    val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()

    private val _currentPlaybackSeconds = MutableStateFlow(0)
    val currentPlaybackSeconds: StateFlow<Int> = _currentPlaybackSeconds.asStateFlow()

    // Current voice settings
    private var currentSpeed: Float = 1.0f
    private var currentPitch: Float = 1.0f

    // Callback invoked when TTS completes playback (used for hands-free loop)
    var onTtsFinished: (() -> Unit)? = null

    init {
        try {
            textToSpeech = TextToSpeech(context.applicationContext, this)
        } catch (_: Exception) {
            isTtsReady = false
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            val hindi = Locale.forLanguageTag("hi-IN")
            val result = textToSpeech?.setLanguage(hindi)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                textToSpeech?.setLanguage(Locale.getDefault())
            }
            textToSpeech?.setSpeechRate(currentSpeed)
            textToSpeech?.setPitch(currentPitch)

            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    scope.launch {
                        _isSpeaking.value = true
                    }
                }

                override fun onDone(utteranceId: String?) {
                    scope.launch {
                        _isSpeaking.value = false
                        _currentPlayingMessageId.value = null
                        _playbackProgress.value = 1f
                        ttsWaveJob?.cancel()
                        _speechRms.value = 0f
                        onTtsFinished?.invoke()
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    scope.launch {
                        _isSpeaking.value = false
                        _currentPlayingMessageId.value = null
                        ttsWaveJob?.cancel()
                        _speechRms.value = 0f
                    }
                }
            })
        }
    }

    fun updateVoiceSettings(speed: Float, pitch: Float, languageMode: String = "हिन्दी") {
        currentSpeed = speed.coerceIn(0.6f, 1.6f)
        currentPitch = pitch.coerceIn(0.7f, 1.4f)
        if (!isTtsReady) return
        try {
            textToSpeech?.setSpeechRate(currentSpeed)
            textToSpeech?.setPitch(currentPitch)
            val locale = if (languageMode.contains("English", ignoreCase = true)) {
                Locale.ENGLISH
            } else {
                Locale.forLanguageTag("hi-IN")
            }
            textToSpeech?.setLanguage(locale)
        } catch (_: Exception) {}
    }

    fun testSampleVoice(sampleText: String, speed: Float, pitch: Float, languageMode: String = "हिन्दी") {
        updateVoiceSettings(speed, pitch, languageMode)
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "test_sample")
        }
        selectBestLocaleForText(sampleText)
        textToSpeech?.speak(sampleText, TextToSpeech.QUEUE_FLUSH, params, "test_sample")
    }

    private fun selectBestLocaleForText(text: String) {
        if (!isTtsReady) return
        val hasHindi = text.any { it in '\u0900'..'\u097F' }
        val targetLocale = if (hasHindi) Locale.forLanguageTag("hi-IN") else Locale.ENGLISH
        try {
            textToSpeech?.setLanguage(targetLocale)
        } catch (_: Exception) {}
    }

    fun startListening(languageTag: String = "hi-IN") {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _isListening.value = false
            return
        }

        stopListening()
        stopPlayback()

        _speechResult.value = null
        _speechPartialText.value = ""

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                    }

                    override fun onBeginningOfSpeech() {}

                    override fun onRmsChanged(rmsdB: Float) {
                        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.1f, 1f)
                        _speechRms.value = normalized
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                        _speechRms.value = 0f
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        _speechRms.value = 0f
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        _speechRms.value = 0f
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()
                        if (!text.isNullOrBlank()) {
                            _speechResult.value = text
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let {
                            _speechPartialText.value = it
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                when (languageTag) {
                    "en-US", "en-IN" -> {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                    }
                    "auto" -> {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
                    }
                    else -> {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                    }
                }
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            speechRecognizer?.startListening(intent)
        } catch (_: Exception) {
            _isListening.value = false
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (_: Exception) {}
        _isListening.value = false
        _speechRms.value = 0f
    }

    fun speak(messageId: Long, text: String, totalDurationSeconds: Int = 30) {
        if (_currentPlayingMessageId.value == messageId && _isSpeaking.value) {
            stopPlayback()
            return
        }

        stopPlayback()
        _currentPlayingMessageId.value = messageId
        _isSpeaking.value = true
        _playbackProgress.value = 0f
        _currentPlaybackSeconds.value = 0

        val cleanText = text
            .replace("[#*_`~]".toRegex(), "")
            .replace("[\uD800-\uDBFF][\uDC00-\uDFFF]".toRegex(), "")

        if (isTtsReady) {
            selectBestLocaleForText(cleanText)
            textToSpeech?.setSpeechRate(currentSpeed)
            textToSpeech?.setPitch(currentPitch)

            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, messageId.toString())
            }
            textToSpeech?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, messageId.toString())
        }

        // Animate simulated speech RMS for the waveform while TTS plays
        ttsWaveJob = scope.launch {
            var stepCount = 0
            while (_isSpeaking.value) {
                stepCount++
                val simulatedRms = 0.35f + ((stepCount % 5) * 0.12f)
                _speechRms.value = simulatedRms
                delay(120)
            }
            _speechRms.value = 0f
        }

        // Track progress slider
        playbackJob = scope.launch {
            val totalSeconds = totalDurationSeconds.coerceAtLeast(8)
            val stepMs = 250L
            val totalSteps = (totalSeconds * 1000) / stepMs
            var step = 0

            while (step < totalSteps && _currentPlayingMessageId.value == messageId && _isSpeaking.value) {
                delay(stepMs)
                step++
                val progress = (step.toFloat() / totalSteps).coerceIn(0f, 1f)
                _playbackProgress.value = progress
                _currentPlaybackSeconds.value = (progress * totalSeconds).toInt()
            }

            if (_currentPlayingMessageId.value == messageId) {
                _playbackProgress.value = 1f
                _isSpeaking.value = false
                _currentPlayingMessageId.value = null
            }
        }
    }

    fun stopPlayback() {
        playbackJob?.cancel()
        playbackJob = null
        ttsWaveJob?.cancel()
        ttsWaveJob = null
        try {
            textToSpeech?.stop()
        } catch (_: Exception) {}
        _isSpeaking.value = false
        _currentPlayingMessageId.value = null
        _playbackProgress.value = 0f
        _currentPlaybackSeconds.value = 0
        _speechRms.value = 0f
    }

    fun cleanUp() {
        stopListening()
        stopPlayback()
        try {
            textToSpeech?.shutdown()
        } catch (_: Exception) {}
    }
}
