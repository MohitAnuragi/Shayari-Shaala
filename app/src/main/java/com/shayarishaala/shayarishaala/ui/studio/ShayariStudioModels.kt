package com.shayarishaala.shayarishaala.ui.studio

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────
//  Background — 30 gradient presets
// ─────────────────────────────────────────────
enum class ShayariBackground(
    val displayName: String,
    val emoji: String,
    val brush: Brush
) {
    ROSE_ROMANCE(
        "Rose Romance", "🌹",
        Brush.linearGradient(listOf(Color(0xFFFF6B8A), Color(0xFFFF8E9B), Color(0xFFFFB3C1)))
    ),
    MIDNIGHT_BLUE(
        "Midnight Blue", "🌌",
        Brush.verticalGradient(listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E)))
    ),
    GOLDEN_HOUR(
        "Golden Hour", "🌅",
        Brush.linearGradient(listOf(Color(0xFFF7971E), Color(0xFFFFD200)))
    ),
    FOREST_WHISPER(
        "Forest Whisper", "🌿",
        Brush.verticalGradient(listOf(Color(0xFF134E5E), Color(0xFF71B280)))
    ),
    LAVENDER_DREAM(
        "Lavender Dream", "💜",
        Brush.linearGradient(listOf(Color(0xFFDA8FFF), Color(0xFF9B59B6), Color(0xFF6C3483)))
    ),
    OCEAN_BREEZE(
        "Ocean Breeze", "🌊",
        Brush.verticalGradient(listOf(Color(0xFF2193B0), Color(0xFF6DD5ED)))
    ),
    SUNSET_GLOW(
        "Sunset Glow", "🌇",
        Brush.linearGradient(listOf(Color(0xFFFF512F), Color(0xFFDD2476)))
    ),
    ROYAL_PURPLE(
        "Royal Purple", "👑",
        Brush.verticalGradient(listOf(Color(0xFF3A1C71), Color(0xFFD76D77), Color(0xFFFFAF7B)))
    ),
    CRIMSON_LOVE(
        "Crimson Love", "❤️",
        Brush.linearGradient(listOf(Color(0xFFCB2D3E), Color(0xFFEF473A)))
    ),
    NIGHT_SKY(
        "Night Sky", "🌙",
        Brush.verticalGradient(listOf(Color(0xFF0D0D0D), Color(0xFF1B1B4B), Color(0xFF0D0D0D)))
    ),
    PEACH_BLOSSOM(
        "Peach Blossom", "🍑",
        Brush.linearGradient(listOf(Color(0xFFFFDDB4), Color(0xFFFFB6A3), Color(0xFFFF8E72)))
    ),
    MINT_FRESH(
        "Mint Fresh", "🌱",
        Brush.verticalGradient(listOf(Color(0xFF43B89C), Color(0xFF00C9FF)))
    ),
    ROSE_GOLD(
        "Rose Gold", "✨",
        Brush.linearGradient(listOf(Color(0xFFBE9B7B), Color(0xFFEECDA3), Color(0xFFD4A0A0)))
    ),
    DEEP_OCEAN(
        "Deep Ocean", "🐋",
        Brush.verticalGradient(listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364)))
    ),
    WARM_EMBER(
        "Warm Ember", "🔥",
        Brush.linearGradient(listOf(Color(0xFFF46B45), Color(0xFFEEA849)))
    ),
    ARCTIC_ICE(
        "Arctic Ice", "❄️",
        Brush.verticalGradient(listOf(Color(0xFFAFD4F0), Color(0xFFEAF6FF), Color(0xFFBDE0F5)))
    ),
    VIOLET_HAZE(
        "Violet Haze", "🪄",
        Brush.linearGradient(listOf(Color(0xFF4776E6), Color(0xFF8E54E9)))
    ),
    COPPER_SHINE(
        "Copper Shine", "🥉",
        Brush.verticalGradient(listOf(Color(0xFFB8733A), Color(0xFFEFD5A8), Color(0xFFB8733A)))
    ),
    CHERRY_BLOSSOM(
        "Cherry Blossom", "🌸",
        Brush.linearGradient(listOf(Color(0xFFFFE4E1), Color(0xFFFFB6C1), Color(0xFFFF92AD)))
    ),
    INDIGO_NIGHTS(
        "Indigo Nights", "🌃",
        Brush.verticalGradient(listOf(Color(0xFF000046), Color(0xFF1CB5E0)))
    ),
    LIME_ENERGY(
        "Lime Energy", "💚",
        Brush.linearGradient(listOf(Color(0xFF56AB2F), Color(0xFFA8E063)))
    ),
    RUBY_RED(
        "Ruby Red", "💎",
        Brush.verticalGradient(listOf(Color(0xFF9D0023), Color(0xFFCB2D3E), Color(0xFF6B0000)))
    ),
    TEAL_PARADISE(
        "Teal Paradise", "🏝️",
        Brush.linearGradient(listOf(Color(0xFF11998E), Color(0xFF38EF7D)))
    ),
    HONEY_GOLD(
        "Honey Gold", "🍯",
        Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500), Color(0xFFFF8C00)))
    ),
    BLACK_LUXURY(
        "Black Luxury", "🖤",
        Brush.linearGradient(listOf(Color(0xFF1A1A1A), Color(0xFF2D2D2D), Color(0xFF000000)))
    ),
    SILVER_MIST(
        "Silver Mist", "🌫️",
        Brush.verticalGradient(listOf(Color(0xFFBDBDBD), Color(0xFFE0E0E0), Color(0xFF9E9E9E)))
    ),
    DESERT_SAND(
        "Desert Sand", "🏜️",
        Brush.linearGradient(listOf(Color(0xFFDEB887), Color(0xFFF4A460), Color(0xFFD2691E)))
    ),
    PURPLE_STORM(
        "Purple Storm", "⛈️",
        Brush.verticalGradient(listOf(Color(0xFF360033), Color(0xFF0B8793)))
    ),
    NEON_NIGHTS(
        "Neon Nights", "🌆",
        Brush.linearGradient(listOf(Color(0xFF00F2FE), Color(0xFF4FACFE)))
    ),
    MINIMAL_WHITE(
        "Minimal White", "🤍",
        Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF5F5F5)))
    )
}

// ─────────────────────────────────────────────
//  Font — wraps system FontFamily
// ─────────────────────────────────────────────
enum class ShayariFont(
    val displayName: String,
    val emoji: String,
    val fontFamily: FontFamily,
    val previewWeight: FontWeight = FontWeight.Normal
) {
    SERIF("Shayari Serif", "📖", FontFamily.Serif),
    SERIF_BOLD("Bold Serif", "📜", FontFamily.Serif, FontWeight.Bold),
    SANS_SERIF("Modern Sans", "✏️", FontFamily.SansSerif),
    CURSIVE("Cursive Flow", "💫", FontFamily.Cursive),
    MONOSPACE("Typewriter", "⌨️", FontFamily.Monospace),
    DEFAULT("Classic", "🖊️", FontFamily.Default),
    DEFAULT_BOLD("Bold Classic", "🔲", FontFamily.Default, FontWeight.Bold)
}

// ─────────────────────────────────────────────
//  Text alignment
// ─────────────────────────────────────────────
enum class TextAlignOption(
    val displayName: String,
    val emoji: String,
    val textAlign: TextAlign
) {
    LEFT("Left", "⬅️", TextAlign.Left),
    CENTER("Center", "↔️", TextAlign.Center),
    RIGHT("Right", "➡️", TextAlign.Right)
}

// ─────────────────────────────────────────────
//  Text size
// ─────────────────────────────────────────────
enum class TextSizeOption(
    val displayName: String,
    val spValue: Float
) {
    SMALL("Small", 14f),
    MEDIUM("Medium", 20f),
    LARGE("Large", 28f)
}

// ─────────────────────────────────────────────
//  Text color
// ─────────────────────────────────────────────
enum class TextColorOption(
    val displayName: String,
    val color: Color
) {
    WHITE("White", Color(0xFFFFFFFF)),
    BLACK("Black", Color(0xFF000000)),
    GOLD("Gold", Color(0xFFFFD700)),
    PINK("Pink", Color(0xFFFF69B4)),
    BLUE("Blue", Color(0xFF87CEEB)),
    RED("Red", Color(0xFFFF4444)),
    CREAM("Cream", Color(0xFFFFF8DC)),
    SILVER("Silver", Color(0xFFE0E0E0))
}

// ─────────────────────────────────────────────
//  Card style applied to the text container
// ─────────────────────────────────────────────
enum class CardStyle(
    val displayName: String,
    val emoji: String
) {
    TRANSPARENT("Transparent", "🌫️"),
    GLASS("Glass", "🪟"),
    SHADOW("Shadow", "🌑"),
    ROUNDED("Rounded", "⬛"),
    LUXURY("Luxury", "✨")
}

// ─────────────────────────────────────────────
//  Export result sealed class
// ─────────────────────────────────────────────
sealed class ExportResult {
    data class Success(val message: String = "Image Saved Successfully ❤️") : ExportResult()
    data class Failure(val message: String) : ExportResult()
}
