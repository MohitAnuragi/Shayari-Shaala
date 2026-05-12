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
            val shayari = geminiService.generateShayari(prompt)
            Result.success(shayari)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

