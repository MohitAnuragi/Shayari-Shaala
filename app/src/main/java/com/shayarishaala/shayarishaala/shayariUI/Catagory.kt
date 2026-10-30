package com.shayarishaala.shayarishaala.shayariUI

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
fun Catagory(navHostController: NavHostController) {
    val context = LocalContext.current
    val entries = remember {
        ContentRepository.getShayariCategories(context).map {
            ContentsEntry(rawTitle = it.title.orEmpty(), count = it.list?.size ?: 0)
        }
    }
    CategoryIndexScreen(
        title = "विषय-सूची",
        subtitle = "Shayari · ${entries.size} categories",
        entries = entries,
        onBack = dropUnlessResumed { navHostController.popBackStack() },
        onOpen = { navHostController.navigate(ShayariRoutingItems.shayriListScreen.withArg(it)) }
    )
}
