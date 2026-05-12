package com.shayarishaala.shayarishaala.daily

import com.shayarishaala.shayarishaala.utils.DailyShayariHelper


object DailyShayariRepository {

    fun getTodayShayari(): String {
        return try {
            val allShayaris = getAllShayaris()
            if (allShayaris.isEmpty()) {
                return "आज की मेहनत ही कल की पहचान बनती है, इसलिए हर दिन को पूरी ईमानदारी से जियो और अपने सपनों को सच करने में लग जाओ।"
            }

            val dailyIndex = DailyShayariHelper.getDailyIndex(allShayaris.size)
            allShayaris[dailyIndex]
        } catch (e: Exception) {
            "आज की मेहनत ही कल की पहचान बनती है, इसलिए हर दिन को पूरी ईमानदारी से जियो और अपने सपनों को सच करने में लग जाओ।"
        }
    }


    private fun getAllShayaris(): List<String> {
        return try {
            val dailyShayariModels = dailyShayariList()
            val allShayaris = mutableListOf<String>()

            for (model in dailyShayariModels) {
                model.list?.let {
                    allShayaris.addAll(it)
                }
            }

            allShayaris
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

}

