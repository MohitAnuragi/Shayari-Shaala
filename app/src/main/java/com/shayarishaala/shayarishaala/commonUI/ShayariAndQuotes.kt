package com.shayarishaala.shayarishaala.commonUI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.shayarishaala.shayarishaala.Model.Routing.ShayariRoutingItems
import com.shayarishaala.shayarishaala.daily.DailyShayariCard
import com.shayarishaala.shayarishaala.daily.DailyShayariRepository
import com.shayarishaala.shayarishaala.data.repository.ContentRepository
import com.shayarishaala.shayarishaala.favorites.FavoriteViewModel
import com.shayarishaala.shayarishaala.ui.components.BentoTile
import com.shayarishaala.shayarishaala.ui.components.BottomBarSpace
import com.shayarishaala.shayarishaala.ui.components.ContentsRow
import com.shayarishaala.shayarishaala.ui.components.pressClick
import com.shayarishaala.shayarishaala.ui.components.rememberEnterOnceState
import com.shayarishaala.shayarishaala.ui.components.staggeredEnter
import com.shayarishaala.shayarishaala.ui.theme.BentoBlack
import com.shayarishaala.shayarishaala.ui.theme.Ink
import com.shayarishaala.shayarishaala.ui.theme.InkMuted
import com.shayarishaala.shayarishaala.ui.theme.Lilac
import com.shayarishaala.shayarishaala.ui.theme.Lime
import com.shayarishaala.shayarishaala.ui.theme.Paper
import com.shayarishaala.shayarishaala.ui.theme.PaperCard
import com.shayarishaala.shayarishaala.ui.theme.Peach
import com.shayarishaala.shayarishaala.utils.PreferenceManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val DAILY_LIMIT = 5
private const val CONTENTS_PREVIEW = 5

/** Home: diary header, bento grid of features, and the first page of the contents. */
@Composable
fun ShayariAndQuotes(navHostController: NavHostController?) {
    val context = LocalContext.current
    val enterState = rememberEnterOnceState()

    val dailyShayari = remember { DailyShayariRepository.getTodayShayari(context) }
    val categories = remember { ContentRepository.getShayariCategories(context) }
    val quoteCount = remember { ContentRepository.getQuoteCategories(context).size }
    val kalamLeft = remember {
        (DAILY_LIMIT - PreferenceManager(context).getGenerationCount()).coerceAtLeast(0)
    }
    val favoriteViewModel: FavoriteViewModel = viewModel()
    val favorites by favoriteViewModel.favoriteList.collectAsStateWithLifecycle()

    val today = remember { LocalDate.now() }
    val hindiDate = remember {
        today.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale("hi", "IN")))
    }
    val shortDate = remember { today.format(DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 18.dp, bottom = 16.dp, start = 4.dp)
                .staggeredEnter(0, enterState)
        ) {
            Text(hindiDate, style = MaterialTheme.typography.bodyMedium, color = InkMuted)
            Text("शायरी शाला", style = MaterialTheme.typography.displaySmall, color = Ink)
        }

        DailyShayariCard(
            dailyShayari = dailyShayari,
            dateLabel = shortDate,
            onOpen = { navHostController?.navigate(ShayariRoutingItems.finalShayriScreen.withArg(it)) },
            modifier = Modifier.staggeredEnter(1, enterState)
        )

        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FeatureTile(
                color = Lilac,
                icon = Icons.Outlined.Edit,
                title = "Kalam AI",
                caption = "आज $kalamLeft बाकी · कोई भी विषय",
                onClick = { navHostController?.navigate(ShayariRoutingItems.kalamScreen.route) },
                modifier = Modifier
                    .weight(1f)
                    .height(156.dp)
                    .staggeredEnter(2, enterState)
            )
            FeatureTile(
                color = Lime,
                icon = Icons.Outlined.Palette,
                title = "Studio",
                caption = "शायरी को कार्ड बनाएं",
                onClick = { navHostController?.navigate(ShayariRoutingItems.studioScreen.withArg(dailyShayari)) },
                modifier = Modifier
                    .weight(1f)
                    .height(156.dp)
                    .staggeredEnter(3, enterState)
            )
        }

        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FeatureTile(
                color = Peach,
                icon = Icons.Filled.FormatQuote,
                title = "Quotes",
                caption = "$quoteCount sets",
                onClick = { navHostController?.navigate(ShayariRoutingItems.quoteScreen.route) },
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp)
                    .staggeredEnter(4, enterState)
            )
            FeatureTile(
                color = PaperCard,
                icon = Icons.Filled.Favorite,
                title = "Saved",
                caption = when (favorites.size) {
                    0 -> "अभी खाली है"
                    1 -> "1 पन्ना"
                    else -> "${favorites.size} पन्ने"
                },
                onClick = { navHostController?.navigate(ShayariRoutingItems.favoritesScreen.route) },
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp)
                    .staggeredEnter(5, enterState)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp, top = 26.dp, bottom = 4.dp)
                .staggeredEnter(6, enterState),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(Modifier.weight(1f)) {
                Text("विषय-सूची", style = MaterialTheme.typography.headlineSmall, color = Ink)
                Text("Contents", style = MaterialTheme.typography.bodySmall, color = InkMuted)
            }
            Text(
                text = "सब ${categories.size} देखें →",
                style = MaterialTheme.typography.labelLarge,
                color = Ink,
                modifier = Modifier
                    .pressClick { navHostController?.navigate(ShayariRoutingItems.categoryScreen.route) }
                    .padding(vertical = 12.dp, horizontal = 4.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .staggeredEnter(7, enterState)
                .clip(RoundedCornerShape(26.dp))
                .background(PaperCard)
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            val preview = categories.take(CONTENTS_PREVIEW)
            preview.forEachIndexed { index, category ->
                val title = category.title.orEmpty()
                ContentsRow(
                    index = index,
                    rawTitle = title,
                    count = category.list?.size ?: 0,
                    showRule = index < preview.lastIndex,
                    onClick = {
                        navHostController?.navigate(ShayariRoutingItems.shayriListScreen.withArg(title))
                    }
                )
            }
        }

        Spacer(
            Modifier
                .navigationBarsPadding()
                .height(BottomBarSpace)
        )
    }
}

@Composable
private fun FeatureTile(
    color: Color,
    icon: ImageVector,
    title: String,
    caption: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BentoTile(color = color, onClick = onClick, onClickLabel = "Open $title", modifier = modifier) {
        TileIcon(icon)
        Spacer(Modifier.weight(1f))
        TileText(title, caption)
    }
}

@Composable
private fun TileIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(BentoBlack),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = PaperCard, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun ColumnScope.TileText(title: String, caption: String) {
    Text(title, style = MaterialTheme.typography.titleLarge, color = BentoBlack)
    Text(caption, style = MaterialTheme.typography.bodySmall, color = Ink.copy(alpha = 0.8f))
}
