package com.shayarishaala.shayarishaala.data.remote

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig

class GeminiService(apiKey: String) {
    private val apiKeyForLogging = apiKey
    private val generativeModel = GenerativeModel(
        modelName = "gemini-3-flash-preview",
        apiKey = apiKey,
        generationConfig = generationConfig {
            temperature = 0.7f
            topP = 0.95f
            maxOutputTokens = 6024
        }
    )

    companion object {
        private const val TAG = "GeminiService"
        private const val SYSTEM_PROMPT = """You are an expert Hindi and Urdu poet.

Write a 2 to 4 line Shayari based on the user input.

RULES:
- Output only Shayari
- Use beautiful Hindi/Urdu Devanagari script (not English)
- Add 1-2 relevant emojis to make it beautiful
- Keep it emotional and meaningful
- Each line separated by newline
- No explanation
- Make it poetic and touching"""
    }

    suspend fun generateShayari(userPrompt: String): String {
        return try {

            if (userPrompt.isBlank()) {
                Log.e(TAG, "❌ Prompt is blank")
                throw IllegalArgumentException("Prompt cannot be empty")
            }

            val finalPrompt = SYSTEM_PROMPT + "\n\nUser Input: " + userPrompt


            val response = generativeModel.generateContent(finalPrompt)



            val generatedText = response.text?.trim() ?: ""

            if (generatedText.isEmpty()) {

                throw Exception("Empty response from API")
            }

            generatedText
        } catch (e: Exception) {
            throw e
        }
    }
}

