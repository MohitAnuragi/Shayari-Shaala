package com.shayarishaala.shayarishaala.favorites

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.shayarishaala.shayarishaala.Model.Routing.ShayariRoutingItems
import com.shayarishaala.shayarishaala.ui.components.ActionStyle
import com.shayarishaala.shayarishaala.ui.components.BottomBarSpace
import com.shayarishaala.shayarishaala.ui.components.CopyButton
import com.shayarishaala.shayarishaala.ui.components.CountBadge
import com.shayarishaala.shayarishaala.ui.components.EmptyNotebook
import com.shayarishaala.shayarishaala.ui.components.HeartButton
import com.shayarishaala.shayarishaala.ui.components.KaagazTopBar
import com.shayarishaala.shayarishaala.ui.components.MoodChip
import com.shayarishaala.shayarishaala.ui.components.NotebookCard
import com.shayarishaala.shayarishaala.ui.components.ShareButton
import com.shayarishaala.shayarishaala.ui.components.SharedKeys
import com.shayarishaala.shayarishaala.ui.components.categoryDisplay
import com.shayarishaala.shayarishaala.ui.components.pressClick
import com.shayarishaala.shayarishaala.ui.components.rememberEnterOnceState
import com.shayarishaala.shayarishaala.ui.components.sharedShayari
import com.shayarishaala.shayarishaala.ui.components.staggeredEnter
import com.shayarishaala.shayarishaala.ui.theme.Motion
import com.shayarishaala.shayarishaala.ui.theme.Paper
import com.shayarishaala.shayarishaala.ui.theme.ShayariTextStyle
import com.shayarishaala.shayarishaala.ui.theme.moodColor
import com.shayarishaala.shayarishaala.utils.ShareUtils

@Composable
fun FavoritesScreen(navHostController: NavHostController?) {
    val context = LocalContext.current
    val favoriteViewModel: FavoriteViewModel = viewModel()
    val favorites by favoriteViewModel.favoriteList.collectAsStateWithLifecycle()
    val enterState = rememberEnterOnceState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
    ) {
        KaagazTopBar(
            title = "संभाली हुई शायरी",
            subtitle = "Saved · newest first",
            onBack = dropUnlessResumed { navHostController?.popBackStack() },
            actions = { if (favorites.isNotEmpty()) CountBadge(favorites.size) }
        )

        Crossfade(
            targetState = favorites.isEmpty(),
            animationSpec = tween(Motion.MEDIUM),
            label = "favoritesEmpty"
        ) { isEmpty ->
            if (isEmpty) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyNotebook(
                        icon = Icons.Outlined.FavoriteBorder,
                        title = "अभी कोई पन्ना नहीं",
                        message = "Tap ♥ on any shayari to save it here"
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    itemsIndexed(favorites, key = { _, item -> item.id }) { index, fav ->
                        val display = categoryDisplay(fav.category)
                        NotebookCard(
                            tilt = if (index % 2 == 0) -0.5f else 0.5f,
                            lineSpacing = 36.sp,
                            firstRuleOffset = 96.dp,
                            modifier = Modifier
                                .animateItem()
                                .staggeredEnter(index, enterState, key = fav.id)
                                .sharedShayari(SharedKeys.single(fav.text))
                                .pressClick(pressedScale = 0.98f, onClickLabel = "Read") {
                                    navHostController?.navigate(ShayariRoutingItems.finalShayriScreen.withArg(fav.text))
                                }
                        ) {
                            MoodChip(
                                text = listOf(display.emoji, display.hindi ?: display.name)
                                    .filter { it.isNotEmpty() }
                                    .joinToString(" "),
                                color = moodColor(fav.category.hashCode())
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = fav.text,
                                style = ShayariTextStyle.copy(fontSize = 20.sp, lineHeight = 36.sp)
                            )
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CopyButton(text = fav.text, style = ActionStyle.Paper)
                                ShareButton(
                                    onClick = { ShareUtils.shareText(context, fav.text) },
                                    style = ActionStyle.Paper
                                )
                                Spacer(Modifier.weight(1f))
                                HeartButton(
                                    isFavorite = true,
                                    onToggle = { favoriteViewModel.removeFavorite(fav.text) },
                                    style = ActionStyle.Paper
                                )
                            }
                        }
                    }
                    item {
                        Spacer(
                            Modifier
                                .navigationBarsPadding()
                                .height(BottomBarSpace)
                        )
                    }
                }
            }
        }
    }
}
