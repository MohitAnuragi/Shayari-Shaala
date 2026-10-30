package com.shayarishaala.shayarishaala.daily

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shayarishaala.shayarishaala.favorites.FavoriteViewModel
import com.shayarishaala.shayarishaala.ui.components.ActionStyle
import com.shayarishaala.shayarishaala.ui.components.BentoTile
import com.shayarishaala.shayarishaala.ui.components.CircleIconButton
import com.shayarishaala.shayarishaala.ui.components.CopyButton
import com.shayarishaala.shayarishaala.ui.components.HeartButton
import com.shayarishaala.shayarishaala.ui.components.MoodChip
import com.shayarishaala.shayarishaala.ui.components.ShareButton
import com.shayarishaala.shayarishaala.ui.theme.BentoBlack
import com.shayarishaala.shayarishaala.ui.theme.Lime
import com.shayarishaala.shayarishaala.ui.theme.OnBlackMuted
import com.shayarishaala.shayarishaala.ui.theme.PaperCard
import com.shayarishaala.shayarishaala.ui.theme.ShayariTextStyle
import com.shayarishaala.shayarishaala.utils.ShareUtils

private const val FALLBACK_SHAYARI =
    "आज की मेहनत ही कल की पहचान बनती है, हर ठोकर ही आगे बढ़ने की वजह बनती है।"

/**
 * Home's black hero tile with today's shayari. It floats gently, and its actions copy, share
 * and save the shayari exactly as before.
 */
@Composable
fun DailyShayariCard(
    dailyShayari: String? = null,
    dateLabel: String,
    onOpen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val shayari = dailyShayari ?: FALLBACK_SHAYARI

    val favoriteViewModel: FavoriteViewModel = viewModel()
    val isFavorite by favoriteViewModel.isFavorite(shayari)
        .collectAsStateWithLifecycle(initialValue = false)

    val float = rememberInfiniteTransition(label = "dailyFloat")
    val lift by float.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "lift"
    )

    BentoTile(
        color = BentoBlack,
        onClick = { onOpen(shayari) },
        onClickLabel = "Read today's shayari",
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { translationY = -3.dp.toPx() * lift },
        contentPadding = 20.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MoodChip(text = "आज की शायरी", color = Lime)
            Text(dateLabel, style = MaterialTheme.typography.bodySmall, color = OnBlackMuted)
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = shayari,
            style = ShayariTextStyle.copy(color = PaperCard, fontSize = 21.sp, lineHeight = 34.sp)
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CopyButton(text = shayari, style = ActionStyle.OnBlack)
            ShareButton(
                onClick = { ShareUtils.shareText(context, shayari, ShareUtils.HEADER_DAILY) },
                style = ActionStyle.OnBlack
            )
            HeartButton(
                isFavorite = isFavorite,
                onToggle = { favoriteViewModel.toggleFavorite(shayari) },
                style = ActionStyle.OnBlack
            )
            Spacer(Modifier.weight(1f))
            CircleIconButton(
                icon = Icons.Filled.NorthEast,
                contentDescription = "Open shayari",
                onClick = { onOpen(shayari) },
                containerColor = PaperCard,
                contentColor = BentoBlack,
                borderColor = null
            )
        }
    }
}
