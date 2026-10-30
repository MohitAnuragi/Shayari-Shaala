package com.shayarishaala.shayarishaala.daily

import android.content.Context
import com.shayarishaala.shayarishaala.data.repository.ContentRepository
import com.shayarishaala.shayarishaala.utils.DailyShayariHelper


object DailyShayariRepository {

    private const val FALLBACK_SHAYARI =
        "आज की मेहनत ही कल की पहचान बनती है, इसलिए हर दिन को पूरी ईमानदारी से जियो और अपने सपनों को सच करने में लग जाओ।"

    fun getTodayShayari(context: Context): String {
        val allShayaris = ContentRepository.getDailyShayari(context)
        if (allShayaris.isEmpty()) return FALLBACK_SHAYARI

        val dailyIndex = DailyShayariHelper.getDailyIndex(allShayaris.size)
        return allShayaris[dailyIndex]
    }
}
