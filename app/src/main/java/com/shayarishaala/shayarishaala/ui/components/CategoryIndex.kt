package com.shayarishaala.shayarishaala.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shayarishaala.shayarishaala.ui.theme.Paper

/** One entry of a contents page: the raw category title and how many items it holds. */
data class ContentsEntry(val rawTitle: String, val count: Int)

/** The diary's contents page, shared by the Shayari and Quotes category screens. */
@Composable
fun CategoryIndexScreen(
    title: String,
    subtitle: String,
    entries: List<ContentsEntry>,
    onBack: () -> Unit,
    onOpen: (rawTitle: String) -> Unit,
) {
    val enterState = rememberEnterOnceState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
    ) {
        KaagazTopBar(title = title, subtitle = subtitle, onBack = onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp)
        ) {
            itemsIndexed(entries, key = { _, entry -> entry.rawTitle }) { index, entry ->
                ContentsRow(
                    index = index,
                    rawTitle = entry.rawTitle,
                    count = entry.count,
                    showRule = index < entries.lastIndex,
                    onClick = { onOpen(entry.rawTitle) },
                    modifier = Modifier.staggeredEnter(index, enterState, key = entry.rawTitle)
                )
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
