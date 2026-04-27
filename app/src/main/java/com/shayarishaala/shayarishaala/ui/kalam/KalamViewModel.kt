package com.shayarishaala.shayarishaala.ui.kalam

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shayarishaala.shayarishaala.data.repository.KalamRepository
import com.shayarishaala.shayarishaala.utils.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

data class KalamUiState(
    val isLoading: Boolean = false,
    val shayari: String = "",
    val error: String? = null,
    val userPrompt: String = "",
    val isFavorite: Boolean = false,
    val generationCount: Int = 0
)

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
        val prompt = _state.value.userPrompt.trim()

        if (prompt.isEmpty()) {
            _state.value = _state.value.copy(error = "Please enter a prompt")
            return
        }

        if (!preferenceManager.canGenerateShayari()) {
            _state.value = _state.value.copy(
                error = "You've reached today's limit (5 generations/day)"
            )
            return
        }

        _state.value = _state.value.copy(isLoading = true, error = null, shayari = "")

        viewModelScope.launch {
            try {
                Log.d(TAG, "Generating shayari with prompt: $prompt")

                val result = repository.generateShayari(prompt)

                result.onSuccess { generatedShayari ->
                    Log.d(TAG, "Success: Generated shayari: $generatedShayari")

                    preferenceManager.incrementGenerationCount()
                    updateGenerationCount()

                    _state.value = _state.value.copy(
                        isLoading = false,
                        shayari = generatedShayari,
                        error = null,
                        isFavorite = preferenceManager.isFavorite(generatedShayari)
                    )
                }.onFailure { exception ->
                    Log.e(TAG, "Failure: ${exception::class.simpleName} - ${exception.message}", exception)

                    val errorMessage = when (exception) {
                        is IllegalArgumentException -> "Invalid input"
                        is UnknownHostException -> "Check your internet connection"
                        is SocketTimeoutException -> "Request timed out, try again"
                        else -> {
                            val msg = exception.message ?: ""
                            when {
                                msg.contains("MAX_TOKENS", ignoreCase = true) -> "Response too long. Try a shorter prompt"
                                msg.contains("404", ignoreCase = true) -> "Model not available. Check API configuration"
                                msg.contains("gemini-pro is not found", ignoreCase = true) -> "⚠️ Model not available. Using gemini-1.5-flash"
                                msg.contains("Empty response", ignoreCase = true) -> "Try a different prompt"
                                msg.contains("429", ignoreCase = true) -> "Rate limited, try later"
                                msg.contains("401", ignoreCase = true) -> "Authentication failed - Check API key"
                                msg.contains("Network", ignoreCase = true) -> "Check your internet connection"
                                msg.contains("Unable to resolve host", ignoreCase = true) -> "Check your internet connection"
                                msg.contains("timeout", ignoreCase = true) -> "Request timed out"
                                msg.contains("Unexpected Response", ignoreCase = true) -> "API Error: ${msg.take(100)}"
                                else -> "API Error: ${msg.take(150)}"
                            }
                        }
                    }

                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = errorMessage,
                        shayari = ""
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error: ${e.message}", e)

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Something went wrong 😔"
                )
            }
        }
    }

    fun regenerate() {
        if (_state.value.userPrompt.isNotEmpty()) {
            generateShayari()
        }
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

