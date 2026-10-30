package com.shayarishaala.shayarishaala.ui.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.shayarishaala.shayarishaala.Model.Routing.ShayariRoutingItems
import com.shayarishaala.shayarishaala.favorites.FavoriteViewModel
import com.shayarishaala.shayarishaala.ui.theme.InkMuted
import com.shayarishaala.shayarishaala.ui.theme.Paper
import com.shayarishaala.shayarishaala.ui.theme.ShayariTextStyle
import com.shayarishaala.shayarishaala.ui.theme.moodColor
import com.shayarishaala.shayarishaala.utils.ShareUtils

/**
 * A category's pages as notebook cards. Shared by shayari and quote lists.
 *
 * @param title    raw category title, used verbatim for the reader route and as the favourite category
 * @param category category saved with a favourite (same values as before the redesign)
 * @param type     [ShayariRoutingItems.finalShayriScreen.TYPE_SHAYARI] or TYPE_QUOTE
 */
@Composable
fun ShayariCollection(
    navHostController: NavHostController?,
    title: String?,
    items: List<String>,
    category: String,
    type: String,
    noun: String,
    moodIndex: Int,
) {
    val context = LocalContext.current
    val favoriteViewModel: FavoriteViewModel = viewModel()
    val enterState = rememberEnterOnceState()
    val display = remember(title) { categoryDisplay(title) }
    val rawTitle = title.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
    ) {
        KaagazTopBar(
            title = display.headline,
            subtitle = "${display.name} · ${items.size} $noun",
            onBack = dropUnlessResumed { navHostController?.popBackStack() },
            actions = {
                if (display.emoji.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(moodColor(moodIndex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(display.emoji, fontSize = 22.sp)
                    }
                }
            }
        )

        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyNotebook(
                    icon = Icons.AutoMirrored.Outlined.MenuBook,
                    title = "अभी कुछ नहीं",
                    message = "Nothing here yet"
                )
            }
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            itemsIndexed(items, key = { index, _ -> index }) { index, item ->
                val isFavorite by favoriteViewModel.isFavorite(item)
                    .collectAsStateWithLifecycle(initialValue = false)
                NotebookCard(
                    tilt = if (index % 2 == 0) -0.6f else 0.6f,
                    showTape = index == 0,
                    lineSpacing = 36.sp,
                    firstRuleOffset = 52.dp,
                    modifier = Modifier
                        .staggeredEnter(index, enterState)
                        .sharedShayari(SharedKeys.inCategory(type, rawTitle, index))
                        .pressClick(pressedScale = 0.98f, onClickLabel = "Read") {
                            navHostController?.navigate(
                                ShayariRoutingItems.finalShayriScreen.reader(item, type, rawTitle, index)
                            )
                        }
                ) {
                    Text(
                        text = item,
                        style = ShayariTextStyle.copy(fontSize = 20.sp, lineHeight = 36.sp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "पन्ना ${index + 1}",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkMuted,
                            modifier = Modifier.weight(1f)
                        )
                        ShareButton(
                            onClick = { ShareUtils.shareText(context, item) },
                            style = ActionStyle.Paper
                        )
                        HeartButton(
                            isFavorite = isFavorite,
                            onToggle = { favoriteViewModel.toggleFavorite(item, category) },
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
