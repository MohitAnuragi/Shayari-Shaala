package com.shayarishaala.shayarishaala.utils

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("kalam_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val GENERATION_COUNT_KEY = "generation_count"
        private const val GENERATION_DATE_KEY = "generation_date"
        private const val FAVORITES_SET_KEY = "favorites_set"
    }

    fun incrementGenerationCount() {
        val today = LocalDate.now().toString()
        val lastDate = sharedPreferences.getString(GENERATION_DATE_KEY, "")

        val count = if (today == lastDate) {
            sharedPreferences.getInt(GENERATION_COUNT_KEY, 0) + 1
        } else {
            1
        }

        sharedPreferences.edit().apply {
            putInt(GENERATION_COUNT_KEY, count)
            putString(GENERATION_DATE_KEY, today)
            apply()
        }
    }

    fun getGenerationCount(): Int {
        val today = LocalDate.now().toString()
        val lastDate = sharedPreferences.getString(GENERATION_DATE_KEY, "")
        return if (today == lastDate) {
            sharedPreferences.getInt(GENERATION_COUNT_KEY, 0)
        } else {
            0
        }
    }

    fun canGenerateShayari(): Boolean {
        return getGenerationCount() < 5
    }

    fun addFavorite(shayari: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.add(shayari)
        sharedPreferences.edit().putStringSet(FAVORITES_SET_KEY, favorites).apply()
    }

    fun removeFavorite(shayari: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.remove(shayari)
        sharedPreferences.edit().putStringSet(FAVORITES_SET_KEY, favorites).apply()
    }

    fun isFavorite(shayari: String): Boolean {
        return getFavorites().contains(shayari)
    }

    fun getFavorites(): Set<String> {
        return sharedPreferences.getStringSet(FAVORITES_SET_KEY, emptySet()) ?: emptySet()
    }
}

