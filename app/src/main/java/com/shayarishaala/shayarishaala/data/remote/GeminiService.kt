package com.shayarishaala.shayarishaala.data.remote

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig

class GeminiService(apiKey: String) {
    private val generativeModel = GenerativeModel(
        // Low-cost model ($0.25 in / $1.50 out per 1M tokens), MINIMAL thinking by default.
        // Google shuts this model down on May 7, 2027 (replacement: gemini-3.5-flash-lite).
        modelName = "gemini-3.1-flash-lite",
        apiKey = apiKey,
        generationConfig = generationConfig {
            temperature = 0.7f
            topP = 0.95f
            // A 2-4 line shayari is ~100-200 tokens; headroom left for minimal thinking.
            // This caps the worst-case cost per call.
            maxOutputTokens = 1024
        },
        // Play's AI-generated content policy: the app must not generate offensive content
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
        )
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
- Make it poetic and touching
- Never write sexual, hateful, harassing or violent content, or content that encourages self-harm"""
    }

    suspend fun generateShayari(userPrompt: String): String {
        return try {

            if (userPrompt.isBlank()) {
                Log.e(TAG, "❌ Prompt is blank")
                throw IllegalArgumentException("Prompt cannot be empty")
            }

            val finalPrompt = SYSTEM_PROMPT + "\n\nUser Input: " + userPrompt


            val response = generativeModel.generateContent(finalPrompt)

            // Billed token counts; total - prompt - output = hidden thinking tokens
            response.usageMetadata?.let {
                Log.d(TAG, "tokens prompt=${it.promptTokenCount} output=${it.candidatesTokenCount} total=${it.totalTokenCount}")
            }


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

