package com.shayarishaala.shayarishaala.data.repository

import android.util.Log
import com.shayarishaala.shayarishaala.data.remote.GeminiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KalamRepository(private val geminiService: GeminiService) {

    companion object {
        private const val TAG = "KalamRepository"
    }

    suspend fun generateShayari(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Repository: Calling geminiService.generateShayari")
            val shayari = geminiService.generateShayari(prompt)
            Log.d(TAG, "Repository: Success - received shayari")
            Result.success(shayari)
        } catch (e: Exception) {
            Log.e(TAG, "Repository: Error - ${e.message}", e)
            Result.failure(e)
        }
    }
}

