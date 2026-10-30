package com.shayarishaala.shayarishaala.quoteUI

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavHostController
import com.shayarishaala.shayarishaala.Model.Routing.ShayariRoutingItems
import com.shayarishaala.shayarishaala.data.repository.ContentRepository
import com.shayarishaala.shayarishaala.ui.components.CategoryIndexScreen
import com.shayarishaala.shayarishaala.ui.components.ContentsEntry

@Composable
fun QuoteScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val entries = remember {
        ContentRepository.getQuoteCategories(context).map {
            ContentsEntry(rawTitle = it.value.orEmpty(), count = it.listValue?.size ?: 0)
        }
    }
    CategoryIndexScreen(
        title = "सुविचार",
        subtitle = "Quotes · ${entries.size} sets",
        entries = entries,
        onBack = dropUnlessResumed { navHostController.popBackStack() },
        onOpen = { navHostController.navigate(ShayariRoutingItems.quoteListItem.withArg(it)) }
    )
}
