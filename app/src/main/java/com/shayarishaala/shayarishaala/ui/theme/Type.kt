package com.shayarishaala.shayarishaala.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.shayarishaala.shayarishaala.R

/** Handwritten Devanagari + Latin, used for shayari text and big headings. */
val KalamFamily = FontFamily(
    Font(R.font.kalam_regular, FontWeight.Normal),
    Font(R.font.kalam_bold, FontWeight.Bold),
)

/** Clean Devanagari + Latin sans, used for all UI text. */
val HindFamily = FontFamily(
    Font(R.font.hind_regular, FontWeight.Normal),
    Font(R.font.hind_medium, FontWeight.Medium),
    Font(R.font.hind_semibold, FontWeight.SemiBold),
    Font(R.font.hind_bold, FontWeight.Bold),
)

/** Style for the shayari itself, wherever it is shown. */
val ShayariTextStyle = TextStyle(
    fontFamily = KalamFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 22.sp,
    lineHeight = 38.sp,
    color = Ink,
)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 44.sp, lineHeight = 52.sp),
    displayMedium = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 38.sp, lineHeight = 46.sp),
    displaySmall = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 34.sp, lineHeight = 42.sp),
    headlineLarge = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge = TextStyle(fontFamily = HindFamily, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = HindFamily, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 24.sp),
    titleSmall = TextStyle(fontFamily = HindFamily, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = HindFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = HindFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = HindFamily, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = HindFamily, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = HindFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 18.sp),
    labelSmall = TextStyle(fontFamily = HindFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
)
