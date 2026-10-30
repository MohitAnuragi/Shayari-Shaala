package com.shayarishaala.shayarishaala.shayariUI

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.shayarishaala.shayarishaala.Model.Routing.ShayariRoutingItems
import com.shayarishaala.shayarishaala.data.repository.ContentRepository
import com.shayarishaala.shayarishaala.ui.components.ShayariCollection

@Composable
fun ShayariListItem(navHostController: NavHostController? = null, title: String?) {
    val context = LocalContext.current
    val categories = remember { ContentRepository.getShayariCategories(context) }
    val categoryIndex = remember(title) { categories.indexOfFirst { it.title == title } }
    val shayariList = remember(title) { categories.getOrNull(categoryIndex)?.list.orEmpty() }

    ShayariCollection(
        navHostController = navHostController,
        title = title,
        items = shayariList,
        category = title ?: "Shayari",
        type = ShayariRoutingItems.finalShayriScreen.TYPE_SHAYARI,
        noun = "shayari",
        moodIndex = categoryIndex
    )
}
