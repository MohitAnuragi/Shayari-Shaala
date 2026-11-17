package com.shayarishaala.shayarishaala.data.repository

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shayarishaala.shayarishaala.data.remote.GeminiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Appears in Crashlytics as a non-fatal issue, so offensive output reaches the developer. */
private class ReportedKalamShayari(message: String) : Exception(message)

class KalamRepository(private val geminiService: GeminiService) {

    suspend fun generateShayari(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val shayari = geminiService.generateShayari(prompt)
            Result.success(shayari)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Sends only the AI text and the reason; the user's prompt is never included. */
    fun reportShayari(shayari: String, reason: String) {
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.setCrashlyticsCollectionEnabled(true)
        crashlytics.recordException(ReportedKalamShayari("$reason: $shayari"))
    }
}

