package com.shayarishaala.shayarishaala.ui.kalam

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.PromptBlockedException
import com.google.ai.client.generativeai.type.QuotaExceededException
import com.google.ai.client.generativeai.type.RequestTimeoutException
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.UnsupportedUserLocationException
import com.shayarishaala.shayarishaala.data.repository.KalamRepository
import com.shayarishaala.shayarishaala.utils.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import java.nio.channels.UnresolvedAddressException

data class KalamUiState(
    val isLoading: Boolean = false,
    val shayari: String = "",
    val error: String? = null,
    val userPrompt: String = "",
    val isFavorite: Boolean = false,
    val generationCount: Int = 0,
    val showConsent: Boolean = false,
    val reportSent: Boolean = false
)

/** A friendly Hindi message for a failed generation; raw API errors never reach the user. */
internal fun kalamErrorMessage(error: Throwable): String {
    val chain = generateSequence(error) { it.cause }.take(8).toList()
    return when {
        chain.any { it is SocketTimeoutException || it is RequestTimeoutException } ->
            "समय ज़्यादा लग गया, दोबारा कोशिश करें"
        chain.any { it is IOException || it is UnresolvedAddressException } ->
            "इंटरनेट कनेक्शन जाँचें और दोबारा कोशिश करें"
        chain.any { it is PromptBlockedException || it is ResponseStoppedException } ->
            "इस विषय पर शायरी नहीं लिखी जा सकती, कोई और विषय आज़माएँ"
        chain.any { it is UnsupportedUserLocationException } ->
            "Kalam AI आपके क्षेत्र में उपलब्ध नहीं है"
        chain.any { it is QuotaExceededException } ->
            "कलम अभी आराम कर रही है, थोड़ी देर बाद कोशिश करें"
        else -> "कलम अभी नहीं लिख पा रही, थोड़ी देर बाद कोशिश करें"
    }
}

class KalamViewModel(
    private val repository: KalamRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    companion object {
        private const val TAG = "KalamViewModel"
    }

    private val _state = MutableStateFlow(KalamUiState())
    val state: StateFlow<KalamUiState> = _state.asStateFlow()

    init {
        updateGenerationCount()
    }

    private fun updateGenerationCount() {
        _state.value = _state.value.copy(
            generationCount = preferenceManager.getGenerationCount()
        )
    }

    fun onPromptChanged(prompt: String) {
        _state.value = _state.value.copy(userPrompt = prompt)
    }

    fun generateShayari() {
        // A second tap must never start a second paid request
        if (_state.value.isLoading) return

        val prompt = _state.value.userPrompt.trim()

        if (prompt.isEmpty()) {
            _state.value = _state.value.copy(error = "कोई विषय लिखिए")
            return
        }

        if (!preferenceManager.canGenerateShayari()) {
            _state.value = _state.value.copy(
                error = "आज की सीमा (5 शायरी) पूरी हो गई"
            )
            return
        }

        if (!preferenceManager.hasAiConsent()) {
            _state.value = _state.value.copy(showConsent = true)
            return
        }

        _state.value = _state.value.copy(isLoading = true, error = null, shayari = "", reportSent = false)

        viewModelScope.launch {
            try {
                val result = repository.generateShayari(prompt)

                result.onSuccess { generatedShayari ->
                    preferenceManager.incrementGenerationCount()
                    updateGenerationCount()

                    _state.value = _state.value.copy(
                        isLoading = false,
                        shayari = generatedShayari,
                        error = null,
                        isFavorite = preferenceManager.isFavorite(generatedShayari)
                    )
                }.onFailure { exception ->
                    // Class name only: messages can carry prompt text or request details
                    Log.e(TAG, "Failure: ${exception::class.simpleName}")

                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = kalamErrorMessage(exception),
                        shayari = ""
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error: ${e::class.simpleName}")

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "कुछ गड़बड़ हो गई 😔"
                )
            }
        }
    }

    fun regenerate() {
        if (_state.value.userPrompt.isNotEmpty()) {
            generateShayari()
        }
    }

    fun acceptConsent() {
        preferenceManager.setAiConsent(true)
        _state.value = _state.value.copy(showConsent = false)
        generateShayari()
    }

    fun declineConsent() {
        _state.value = _state.value.copy(showConsent = false)
    }

    /** Sends the report and removes the shayari from the screen. */
    fun reportShayari(reason: String) {
        val currentShayari = _state.value.shayari
        if (currentShayari.isEmpty()) return
        repository.reportShayari(currentShayari, reason)
        _state.value = _state.value.copy(shayari = "", isFavorite = false, reportSent = true)
    }

    fun toggleFavorite() {
        val currentShayari = _state.value.shayari
        if (currentShayari.isNotEmpty()) {
            val newFavoriteState = !_state.value.isFavorite
            if (newFavoriteState) {
                preferenceManager.addFavorite(currentShayari)
            } else {
                preferenceManager.removeFavorite(currentShayari)
            }
            _state.value = _state.value.copy(isFavorite = newFavoriteState)
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}

