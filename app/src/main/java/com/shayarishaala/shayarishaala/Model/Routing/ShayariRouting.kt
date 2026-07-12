package com.shayarishaala.shayarishaala.Model.Routing

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.shayarishaala.shayarishaala.commonUI.FinalShayriView
import com.shayarishaala.shayarishaala.commonUI.FloatingBottomButton
import com.shayarishaala.shayarishaala.commonUI.ShayariAndQuotes
import com.shayarishaala.shayarishaala.commonUI.SplashScreen
import com.shayarishaala.shayarishaala.favorites.FavoritesScreen
import com.shayarishaala.shayarishaala.quoteUI.QuoteListItem
import com.shayarishaala.shayarishaala.quoteUI.QuoteScreen
import com.shayarishaala.shayarishaala.shayariUI.Catagory
import com.shayarishaala.shayarishaala.shayariUI.ShayariListItem
import android.net.Uri
import com.shayarishaala.shayarishaala.ui.kalam.KalamScreen
import com.shayarishaala.shayarishaala.ui.studio.ShayariStudioScreen

@Composable
fun ShayariRouting(navHostController: NavHostController) {

    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define logic for showing the FAB
    val showFab = when {
        currentRoute == null -> false
        currentRoute == ShayariRoutingItems.splashScreen.route -> false
        // Hide on Detail Screen (final_shayari/{item})
        currentRoute.contains(ShayariRoutingItems.finalShayriScreen.route) -> false
        // Hide when already on the Kalam (creation) screen
        currentRoute == ShayariRoutingItems.kalamScreen.route -> false
        // Hide on Favorites screen
        currentRoute == ShayariRoutingItems.favoritesScreen.route -> false
        // Hide on Shayari Studio screen
        currentRoute?.startsWith(ShayariRoutingItems.studioScreen.route) == true -> false
        else -> true
    }

    Scaffold(
        floatingActionButton = {
            if (showFab) {
                FloatingBottomButton(navHostController = navHostController)
            }
        }
    ) { _ -> // Inner padding ignored to avoid UI shifts
        NavHost(
            navController = navHostController,
            startDestination = ShayariRoutingItems.splashScreen.route
        ) {
            composable(ShayariRoutingItems.shayariAndQuotes.route) {
                ShayariAndQuotes(navHostController)
            }
            composable(ShayariRoutingItems.categoryScreen.route) {
                Catagory(navHostController)
            }
            composable(ShayariRoutingItems.quoteScreen.route) {
                QuoteScreen(navHostController)
            }
            composable(ShayariRoutingItems.splashScreen.route) {
                SplashScreen(navHostController)
            }
            composable(ShayariRoutingItems.shayriListScreen.route + "/{title}") {
                val title = it.arguments?.getString("title")
                ShayariListItem(navHostController, title)
            }
            composable(ShayariRoutingItems.quoteListItem.route + "/{title}") {
                val title = it.arguments?.getString("title")
                QuoteListItem(navHostController, title)
            }
            composable(ShayariRoutingItems.finalShayriScreen.route + "/{item}") {
                val value = it.arguments?.getString("item").toString()
                FinalShayriView(
                    finalShayari = value,
                    navHostController = navHostController
                )
            }
            composable(ShayariRoutingItems.kalamScreen.route) {
                KalamScreen(navHostController = navHostController)
            }
            composable(ShayariRoutingItems.favoritesScreen.route) {
                FavoritesScreen(navHostController)
            }
            composable(ShayariRoutingItems.studioScreen.route + "/{shayariText}") {
                val encoded = it.arguments?.getString("shayariText") ?: ""
                val text = Uri.decode(encoded)
                ShayariStudioScreen(navHostController = navHostController, shayariText = text)
            }
        }
    }
}
