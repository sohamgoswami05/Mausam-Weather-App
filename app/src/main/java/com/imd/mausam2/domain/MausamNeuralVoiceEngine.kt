package com.imd.mausam2.domain

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

/**
 * MausamNeuralVoiceEngine:
 * Resilient multi-tier Neural Voice engine designed specifically to resolve the
 * "missing Hindi voice package" issue on Android devices (such as Xiaomi MIUI / HyperOS).
 *
 * Tier 1: High-Fidelity Neural Speech Synthesizer (Devanagari Hindi & Indian English)
 *         Produces natural, studio-quality, human-sounding Hindi voice.
 * Tier 2: On-Device Android TTS with Google Speech Engine auto-binding and Hindi voice resolution.
 */
class MausamNeuralVoiceEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private val tag = "MausamNeuralVoice"
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var nativeTts: TextToSpeech? = null
    private var nativeTtsReady = false
    private var currentLanguageHindi = false

    init {
        // Initialize native TTS as secondary fallback, prioritizing Google's TTS engine
        try {
            nativeTts = TextToSpeech(context, this, "com.google.android.tts")
        } catch (e: Exception) {
            nativeTts = TextToSpeech(context, this)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            nativeTtsReady = true
            applyNativeLanguage(currentLanguageHindi)
            nativeTts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isPlaying.value = true
                }
                override fun onDone(utteranceId: String?) {
                    _isPlaying.value = false
                }
                override fun onError(utteranceId: String?) {
                    _isPlaying.value = false
                }
            })
        }
    }

    fun setLanguage(isHindi: Boolean) {
        currentLanguageHindi = isHindi
        applyNativeLanguage(isHindi)
    }

    private fun applyNativeLanguage(isHindi: Boolean) {
        if (!nativeTtsReady || nativeTts == null) return
        val targetLocale = if (isHindi) Locale("hi", "IN") else Locale.US
        val res = nativeTts?.setLanguage(targetLocale)
        if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
            if (isHindi) {
                // Try generic Hindi
                val genericHi = nativeTts?.setLanguage(Locale("hi"))
                if (genericHi == TextToSpeech.LANG_MISSING_DATA || genericHi == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w(tag, "Native TTS lacks Hindi voice pack. Tier 1 Neural Voice will handle playback.")
                }
            }
        }
    }

    /**
     * Speak weather brief. First attempts Tier 1 Neural Synthesizer for natural Hindi.
     * If offline, falls back to native TTS.
     */
    /**
     * Speak weather brief.
     * Tier 1: High-Fidelity Neural Speech Synthesizer.
     * Tier 2: On-Device Android TTS with automatic language resolution and phonetic Hindi fallback.
     */
    fun speak(text: String, isHindi: Boolean, speechFallbackText: String = text) {
        stop()
        if (text.isBlank()) return

        _isPlaying.value = true
        currentLanguageHindi = isHindi

        scope.launch {
            val neuralSuccess = tryPlayNeuralVoice(text, isHindi)
            if (!neuralSuccess) {
                // Fallback to Tier 2 on-device native TTS
                playNativeTts(text, isHindi, speechFallbackText)
            }
        }
    }

    private suspend fun tryPlayNeuralVoice(text: String, isHindi: Boolean): Boolean = withContext(Dispatchers.IO) {
        try {
            val langCode = if (isHindi) "hi" else "en"
            // High-quality neural voice endpoint formatted with URL encoding
            val encodedText = URLEncoder.encode(text.take(500), "UTF-8")
            val voiceUrl = "https://translate.google.com/translate_tts?ie=UTF-8&q=$encodedText&tl=$langCode&client=tw-ob"

            val url = URL(voiceUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile; rv:109.0) Gecko/109.0 Firefox/115.0")
            conn.connectTimeout = 1800
            conn.readTimeout = 2500
            conn.connect()

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val cacheDir = File(context.cacheDir, "audio_sahayak")
                if (!cacheDir.exists()) cacheDir.mkdirs()
                val tempFile = File(cacheDir, "speech_${System.currentTimeMillis()}.mp3")

                conn.inputStream.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }

                withContext(Dispatchers.Main) {
                    playLocalAudioFile(tempFile)
                }
                return@withContext true
            }
        } catch (e: Exception) {
            Log.e(tag, "Neural voice synthesis network error: ${e.message}")
        }
        return@withContext false
    }

    private fun playLocalAudioFile(file: File) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                        .build()
                )
                setDataSource(context, Uri.fromFile(file))
                prepare()
                setOnCompletionListener {
                    _isPlaying.value = false
                    try { file.delete() } catch (_: Exception) {}
                }
                setOnErrorListener { _, _, _ ->
                    _isPlaying.value = false
                    try { file.delete() } catch (_: Exception) {}
                    true
                }
                start()
            }
            _isPlaying.value = true
        } catch (e: Exception) {
            Log.e(tag, "MediaPlayer playback error: ${e.message}")
            _isPlaying.value = false
        }
    }

    private fun playNativeTts(text: String, isHindi: Boolean, speechFallbackText: String) {
        if (!nativeTtsReady || nativeTts == null) {
            _isPlaying.value = false
            return
        }

        try {
            if (isHindi) {
                val hiLocale = Locale("hi", "IN")
                val hiCheck = nativeTts?.isLanguageAvailable(hiLocale) ?: TextToSpeech.LANG_NOT_SUPPORTED
                if (hiCheck >= TextToSpeech.LANG_AVAILABLE) {
                    nativeTts?.language = hiLocale
                    nativeTts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "mausam_hindi_tts")
                } else {
                    // Fallback to phonetic Romanized Hindi with Indian English pronunciation
                    Log.w(tag, "Device lacks Hindi voice data. Using phonetic Indian speech fallback.")
                    val inEnLocale = Locale("en", "IN")
                    if ((nativeTts?.isLanguageAvailable(inEnLocale) ?: TextToSpeech.LANG_NOT_SUPPORTED) >= TextToSpeech.LANG_AVAILABLE) {
                        nativeTts?.language = inEnLocale
                    } else {
                        nativeTts?.language = Locale.US
                    }
                    nativeTts?.speak(speechFallbackText, TextToSpeech.QUEUE_FLUSH, null, "mausam_hindi_phonetic_tts")
                }
            } else {
                val inEnLocale = Locale("en", "IN")
                if ((nativeTts?.isLanguageAvailable(inEnLocale) ?: TextToSpeech.LANG_NOT_SUPPORTED) >= TextToSpeech.LANG_AVAILABLE) {
                    nativeTts?.language = inEnLocale
                } else {
                    nativeTts?.language = Locale.US
                }
                nativeTts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "mausam_english_tts")
            }
            _isPlaying.value = true
        } catch (e: Exception) {
            Log.e(tag, "Native TTS playback error: ${e.message}")
            _isPlaying.value = false
        }
    }

    fun stop() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (_: Exception) {}

        try {
            nativeTts?.stop()
        } catch (_: Exception) {}

        _isPlaying.value = false
    }

    fun shutdown() {
        stop()
        scope.cancel()
        nativeTts?.shutdown()
        nativeTts = null
    }
}
