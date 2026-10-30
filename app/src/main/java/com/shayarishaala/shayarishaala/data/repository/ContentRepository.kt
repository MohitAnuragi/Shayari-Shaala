package com.shayarishaala.shayarishaala.data.repository

import android.content.Context
import android.util.Log
import com.shayarishaala.shayarishaala.Model.ShayariModel
import com.shayarishaala.shayarishaala.quoteUI.QuoteModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

/**
 * Loads the app's shayari, quotes and daily shayari from JSON files in `assets/`.
 *
 * - `shayari.json` / `quotes.json`: `[ { "title": "...", "items": ["...", ...] }, ... ]`
 * - `daily_shayari.json`: `["...", "...", ...]`
 *
 * Each file is parsed once and kept in memory. [preload] reads them off the main thread
 * during the splash screen so the lists open instantly.
 */
object ContentRepository {

    private const val TAG = "ContentRepository"
    private const val SHAYARI_FILE = "shayari.json"
    private const val QUOTES_FILE = "quotes.json"
    private const val DAILY_FILE = "daily_shayari.json"

    @Volatile private var shayari: List<ShayariModel>? = null
    @Volatile private var quotes: List<QuoteModel>? = null
    @Volatile private var daily: List<String>? = null

    fun getShayariCategories(context: Context): List<ShayariModel> =
        shayari ?: synchronized(this) {
            shayari ?: readCategories(context, SHAYARI_FILE)
                .map { (title, items) -> ShayariModel(title, items) }
                .also { shayari = it }
        }

    fun getQuoteCategories(context: Context): List<QuoteModel> =
        quotes ?: synchronized(this) {
            quotes ?: readCategories(context, QUOTES_FILE)
                .map { (title, items) -> QuoteModel(title, items) }
                .also { quotes = it }
        }

    fun getDailyShayari(context: Context): List<String> =
        daily ?: synchronized(this) {
            daily ?: readStrings(context, DAILY_FILE).also { daily = it }
        }

    /** Parses all content files on a background thread. */
    suspend fun preload(context: Context) = withContext(Dispatchers.IO) {
        getShayariCategories(context)
        getQuoteCategories(context)
        getDailyShayari(context)
    }

    private fun readCategories(context: Context, fileName: String): List<Pair<String, List<String>>> =
        try {
            val array = JSONArray(readAsset(context, fileName))
            List(array.length()) { i ->
                val category = array.getJSONObject(i)
                category.getString("title") to category.getJSONArray("items").toStringList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load $fileName", e)
            emptyList()
        }

    private fun readStrings(context: Context, fileName: String): List<String> =
        try {
            JSONArray(readAsset(context, fileName)).toStringList()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load $fileName", e)
            emptyList()
        }

    private fun readAsset(context: Context, fileName: String): String =
        context.assets.open(fileName).bufferedReader(Charsets.UTF_8).use { it.readText() }

    private fun JSONArray.toStringList(): List<String> = List(length()) { getString(it) }
}
