package com.shayarishaala.shayarishaala.utils

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Utility object to manage Daily Shayari selection
 * Ensures same shayari is shown to all users on the same day
 */
object DailyShayariHelper {

    /**
     * Get the daily shayari index based on current date
     * Formula: dayOfYear % totalCount = consistent daily selection
     */
    fun getDailyIndex(totalCount: Int): Int {
        if (totalCount <= 0) return 0

        val today = LocalDate.now()
        val dayOfYear = today.dayOfYear.toLong()

        return (dayOfYear % totalCount).toInt()
    }

    /**
     * Get days since app started tracking (for metrics)
     */
    fun getDaysSinceStart(): Long {
        val startDate = LocalDate.of(2026, 1, 1) // App launch date
        val today = LocalDate.now()
        return ChronoUnit.DAYS.between(startDate, today)
    }

    /**
     * Check if it's a new day (for UI refresh purposes)
     */
    fun isNewDay(lastShownDate: String?): Boolean {
        if (lastShownDate == null) return true
        return lastShownDate != LocalDate.now().toString()
    }
}

