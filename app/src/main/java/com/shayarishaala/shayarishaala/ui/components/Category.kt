package com.shayarishaala.shayarishaala.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shayarishaala.shayarishaala.ui.theme.Ink
import com.shayarishaala.shayarishaala.ui.theme.InkMuted
import com.shayarishaala.shayarishaala.ui.theme.KalamFamily
import com.shayarishaala.shayarishaala.ui.theme.DashRule
import com.shayarishaala.shayarishaala.ui.theme.moodColor

/**
 * How a raw content title (e.g. "LOVE 💖") is shown. Display only: the raw title is still what
 * goes into navigation args, list lookups and the saved favourite category.
 */
data class CategoryDisplay(val name: String, val emoji: String, val hindi: String?) {
    /** Big handwritten label: the Hindi word when known, otherwise the English name. */
    val headline: String get() = hindi ?: name
}

private val HINDI_NAMES = mapOf(
    "LOVE" to "इश्क़",
    "FUNNY" to "हँसी",
    "ATTITUDE" to "तेवर",
    "MOTIVATION" to "हौसला",
    "MOTIVATIONAL" to "हौसला",
    "SAD" to "दर्द",
    "BREAKUP" to "जुदाई",
    "DEEP LEARNING" to "गहरी बातें",
    "VALENTINE DAY" to "वैलेंटाइन",
    "FRIEND" to "दोस्ती",
    "FRIENDS" to "दोस्ती",
    "GOOD MORNING" to "सुप्रभात",
    "GOOD NIGHT" to "शुभ रात्रि",
    "NEW YEAR" to "नया साल",
    "BIRTHDAY" to "जन्मदिन",
    "REPUBLIC DAY" to "गणतंत्र दिवस",
    "DIWALI" to "दीवाली",
    "MONEY" to "पैसा",
    "SUCCESS" to "कामयाबी",
    "MOM" to "माँ",
    "SPORTS" to "खेल",
    "2 LINE QUOTES" to "दो पंक्तियाँ",
)

fun categoryDisplay(rawTitle: String?): CategoryDisplay {
    val trimmed = rawTitle.orEmpty().trim()
    val cut = trimmed.indexOfFirst { !(it.isLetterOrDigit() || it == ' ') }
    val namePart = (if (cut < 0) trimmed else trimmed.substring(0, cut)).trim()
    val emoji = if (cut < 0) "" else trimmed.substring(cut).trim()
    val name = namePart.lowercase().split(' ')
        .filter { it.isNotEmpty() }
        .joinToString(" ") { word -> word.replaceFirstChar { it.titlecase() } }
    return CategoryDisplay(name = name, emoji = emoji, hindi = HINDI_NAMES[namePart.uppercase()])
}

/** One line of the diary's "विषय-सूची" (contents page). */
@Composable
fun ContentsRow(
    index: Int,
    rawTitle: String,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showRule: Boolean = true,
) {
    val display = categoryDisplay(rawTitle)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .pressClick(pressedScale = 0.98f, onClickLabel = "Open ${display.name}", onClick = onClick)
            .then(if (showRule) Modifier.dashedBottomRule(DashRule) else Modifier)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(moodColor(index)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = display.emoji.ifEmpty { "%02d".format(index + 1) },
                fontSize = if (display.emoji.isEmpty()) 14.sp else 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Ink
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = display.headline,
                fontFamily = KalamFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 21.sp,
                lineHeight = 26.sp,
                color = Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (display.hindi != null) {
                Text(
                    text = display.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = InkMuted
                )
            }
        }
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = InkMuted
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = InkMuted
        )
    }
}

/** Small rounded label, tinted by mood. */
@Composable
fun MoodChip(text: String, color: Color, modifier: Modifier = Modifier, textColor: Color = Ink) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color)
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = textColor)
    }
}
