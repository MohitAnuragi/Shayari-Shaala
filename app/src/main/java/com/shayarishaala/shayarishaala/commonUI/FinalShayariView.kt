package com.shayarishaala.shayarishaala.commonUI

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.shayarishaala.shayarishaala.Model.Routing.ShayariRoutingItems
import com.shayarishaala.shayarishaala.data.repository.ContentRepository
import com.shayarishaala.shayarishaala.favorites.FavoriteViewModel
import com.shayarishaala.shayarishaala.ui.components.ActionButton
import com.shayarishaala.shayarishaala.ui.components.ActionStyle
import com.shayarishaala.shayarishaala.ui.components.CircleIconButton
import com.shayarishaala.shayarishaala.ui.components.CopyButton
import com.shayarishaala.shayarishaala.ui.components.HeartButton
import com.shayarishaala.shayarishaala.ui.components.NotebookCard
import com.shayarishaala.shayarishaala.ui.components.PillButton
import com.shayarishaala.shayarishaala.ui.components.SharedKeys
import com.shayarishaala.shayarishaala.ui.components.categoryDisplay
import com.shayarishaala.shayarishaala.ui.components.sharedShayari
import com.shayarishaala.shayarishaala.ui.theme.Ink
import com.shayarishaala.shayarishaala.ui.theme.InkMuted
import com.shayarishaala.shayarishaala.ui.theme.Lime
import com.shayarishaala.shayarishaala.ui.theme.Motion
import com.shayarishaala.shayarishaala.ui.theme.Paper
import com.shayarishaala.shayarishaala.ui.theme.RuleLine
import com.shayarishaala.shayarishaala.ui.theme.ShayariTextStyle
import com.shayarishaala.shayarishaala.ui.theme.Sindoor
import com.shayarishaala.shayarishaala.utils.ShareUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

/**
 * Reader: one shayari per notebook page. When opened from a category ([type] + [title]) the user
 * can swipe through the whole category starting at [index]; otherwise it shows just [finalShayari].
 * Copy, save, share and Studio always act on the page currently shown.
 */
@Composable
fun FinalShayriView(
    finalShayari: String,
    navHostController: NavHostController? = null,
    type: String? = null,
    title: String? = null,
    index: Int = -1,
) {
    val context = LocalContext.current
    val favoriteViewModel: FavoriteViewModel = viewModel()

    val categoryItems = remember(type, title) {
        when (type) {
            ShayariRoutingItems.finalShayriScreen.TYPE_SHAYARI ->
                ContentRepository.getShayariCategories(context).firstOrNull { it.title == title }?.list
            ShayariRoutingItems.finalShayriScreen.TYPE_QUOTE ->
                ContentRepository.getQuoteCategories(context).firstOrNull { it.value == title }?.listValue
            else -> null
        }.orEmpty()
    }
    val startPage = remember(categoryItems, finalShayari, index) {
        when {
            index in categoryItems.indices && categoryItems[index] == finalShayari -> index
            else -> categoryItems.indexOf(finalShayari)
        }
    }
    val inCategory = startPage >= 0
    val pages = if (inCategory) categoryItems else listOf(finalShayari)

    val pagerState = rememberPagerState(initialPage = startPage.coerceAtLeast(0)) { pages.size }
    val current = pages[pagerState.currentPage.coerceIn(pages.indices)]
    val isFavorite by remember(current) { favoriteViewModel.isFavorite(current) }
        .collectAsStateWithLifecycle(initialValue = false)

    val display = remember(title) { categoryDisplay(title) }
    val stampText = if (inCategory) display.name.uppercase() else "SHAYARI"
    val dateText = remember { LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Header: back, category and an animated page counter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = dropUnlessResumed { navHostController?.popBackStack() }
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (inCategory) display.headline else "शायरी",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Ink
                )
                if (pages.size > 1) {
                    AnimatedContent(
                        targetState = pagerState.currentPage,
                        transitionSpec = {
                            val forward = targetState > initialState
                            (slideInVertically(tween(Motion.MEDIUM)) { if (forward) it else -it } + fadeIn())
                                .togetherWith(slideOutVertically(tween(Motion.MEDIUM)) { if (forward) -it else it } + fadeOut())
                        },
                        label = "pageCounter"
                    ) { page ->
                        Text(
                            text = "पन्ना ${page + 1} / ${pages.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkMuted
                        )
                    }
                }
            }
            Spacer(Modifier.width(48.dp))
        }

        if (pages.size > 1) {
            val progress by animateFloatAsState(
                targetValue = (pagerState.currentPage + 1f) / pages.size,
                animationSpec = tween(Motion.MEDIUM, easing = Motion.EmphasizedDecelerate),
                label = "readProgress"
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(RuleLine)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(Ink)
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 18.dp),
            pageSpacing = 16.dp,
            key = { it }
        ) { page ->
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val turn = pageOffset.coerceIn(-1f, 1f)
            NotebookCard(
                showTape = true,
                lineSpacing = 44.sp,
                firstRuleOffset = 122.dp,
                contentPadding = 24.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Page-turn: the page hinges on its outer edge, tilts and dims as it leaves
                        val amount = turn.absoluteValue
                        transformOrigin = TransformOrigin(if (turn > 0f) 1f else 0f, 0.5f)
                        rotationY = turn * 10f
                        cameraDistance = 14f * density
                        val scale = lerp(1f, 0.92f, amount)
                        scaleX = scale
                        scaleY = scale
                        alpha = lerp(1f, 0.55f, amount)
                    }
                    .sharedShayari(
                        if (inCategory) SharedKeys.inCategory(type.orEmpty(), title.orEmpty(), page)
                        else SharedKeys.single(finalShayari)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(dateText, style = MaterialTheme.typography.bodySmall, color = InkMuted)
                    Text(
                        text = stampText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Sindoor,
                        modifier = Modifier
                            .rotate(-4f)
                            .border(1.5.dp, Sindoor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
                Spacer(Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = pages[page],
                        style = ShayariTextStyle.copy(fontSize = 25.sp, lineHeight = 44.sp)
                    )
                }
                Text(
                    text = "— शायरी शाला",
                    style = ShayariTextStyle.copy(fontSize = 18.sp, lineHeight = 26.sp, color = InkMuted),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = pages.size > 1 && pagerState.currentPage == startPage && !pagerState.isScrollInProgress,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = "← पन्ना पलटने के लिए स्वाइप करें →",
                style = MaterialTheme.typography.bodySmall,
                color = InkMuted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        // Bento actions for the current page
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CopyButton(text = current, style = ActionStyle.Cell, modifier = Modifier.weight(1f))
                HeartButton(
                    isFavorite = isFavorite,
                    onToggle = {
                        // Pages opened from a category remember it, so Saved can show where they came from
                        if (inCategory && title != null) favoriteViewModel.toggleFavorite(current, title)
                        else favoriteViewModel.toggleFavorite(current)
                    },
                    style = ActionStyle.Cell,
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    icon = Icons.Outlined.Palette,
                    label = "Studio",
                    onClick = { navHostController?.navigate(ShayariRoutingItems.studioScreen.withArg(current)) },
                    style = ActionStyle.Cell,
                    container = Lime,
                    modifier = Modifier.weight(1f)
                )
            }
            PillButton(
                text = "Share",
                icon = Icons.Outlined.Share,
                onClick = { ShareUtils.shareText(context, current) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
