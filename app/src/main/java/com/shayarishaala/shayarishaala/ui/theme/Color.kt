package com.shayarishaala.shayarishaala.ui.theme

import androidx.compose.ui.graphics.Color

// Kaagaz (paper diary) palette
val Paper = Color(0xFFF4EDE0)
val PaperCard = Color(0xFFFFFBF3)
val PaperDeep = Color(0xFFEFE5D3)
val Ink = Color(0xFF2B2620)
val InkMuted = Color(0xFF6E6355)
val RuleLine = Color(0xFFE6DCCB)
val DashRule = Color(0xFFCDBFA8)
val Tape = Color(0xBFD6C4A0)
val Sindoor = Color(0xFFB23A0E)

// Bento palette
val BentoBlack = Color(0xFF151515)
val BentoLine = Color(0xFF3A3A36)
val Lime = Color(0xFFD4F45C)
val Lilac = Color(0xFFC9B8FF)
val Peach = Color(0xFFFFB86B)
val OnBlackMuted = Color(0xFFBDBDB5)

// Mood pastels, cycled by category index
val MoodColors = listOf(
    Color(0xFFFFE1DC),
    Color(0xFFDCE7FF),
    Color(0xFFE2F3D6),
    Color(0xFFFFE9C7),
    Color(0xFFEEE6FF),
)

fun moodColor(index: Int): Color = MoodColors[Math.floorMod(index, MoodColors.size)]
