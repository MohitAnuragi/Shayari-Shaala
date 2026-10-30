package com.shayarishaala.shayarishaala.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/** Single light "paper + ink" scheme; the app has no dark or dynamic-colour variant by design. */
private val KaagazColorScheme = lightColorScheme(
    primary = BentoBlack,
    onPrimary = PaperCard,
    primaryContainer = Lime,
    onPrimaryContainer = BentoBlack,
    secondary = Lilac,
    onSecondary = BentoBlack,
    secondaryContainer = Lilac,
    onSecondaryContainer = BentoBlack,
    tertiary = Sindoor,
    onTertiary = PaperCard,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = PaperDeep,
    onSurfaceVariant = InkMuted,
    surfaceContainer = PaperCard,
    surfaceContainerLow = PaperCard,
    surfaceContainerHigh = PaperDeep,
    outline = InkMuted,
    outlineVariant = RuleLine,
    error = Sindoor,
    onError = PaperCard,
    inverseSurface = BentoBlack,
    inverseOnSurface = PaperCard,
)

@Composable
fun ShayariShaalaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KaagazColorScheme,
        typography = Typography,
        content = content
    )
}
