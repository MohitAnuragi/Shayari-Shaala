package com.shayarishaala.shayarishaala.quoteUI

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.shayarishaala.shayarishaala.Model.Routing.ShayariRoutingItems
import com.shayarishaala.shayarishaala.data.repository.ContentRepository
import com.shayarishaala.shayarishaala.ui.components.ShayariCollection

@Composable
fun QuoteListItem(navHostController: NavHostController? = null, value: String?) {
    val context = LocalContext.current
    val categories = remember { ContentRepository.getQuoteCategories(context) }
    val categoryIndex = remember(value) { categories.indexOfFirst { it.value == value } }
    val quoteList = remember(value) { categories.getOrNull(categoryIndex)?.listValue.orEmpty() }

    ShayariCollection(
        navHostController = navHostController,
        title = value,
        items = quoteList,
        category = value ?: "Quote",
        type = ShayariRoutingItems.finalShayriScreen.TYPE_QUOTE,
        noun = "quotes",
        moodIndex = categoryIndex
    )
}
