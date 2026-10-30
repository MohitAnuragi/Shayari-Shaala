package com.shayarishaala.shayarishaala.utils

import android.content.Context
import android.content.Intent

/** One place for the text share format, so every screen shares shayari the same way. */
object ShareUtils {

    const val PLAY_STORE_URL =
        "https://play.google.com/store/apps/details?id=com.shayarishaala.shayarishaala"

    const val HEADER_SHAYARI = "❤️ Shayari:"
    const val HEADER_DAILY = "🌟 Today's Shayari:"

    fun shareText(context: Context, text: String, header: String = HEADER_SHAYARI) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "$header\n\n$text\n\n📱 Shayari Shaala App:\n$PLAY_STORE_URL"
            )
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Shayari"))
    }
}
