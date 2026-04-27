package com.shayarishaala.shayarishaala.Model.Routing

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shayarishaala.shayarishaala.commonUI.FinalShayriView
import com.shayarishaala.shayarishaala.commonUI.ShayariAndQuotes
import com.shayarishaala.shayarishaala.commonUI.SplashScreen
import com.shayarishaala.shayarishaala.quoteUI.QuoteListItem
import com.shayarishaala.shayarishaala.quoteUI.QuoteScreen
import com.shayarishaala.shayarishaala.shayariUI.Catagory
import com.shayarishaala.shayarishaala.shayariUI.ShayariListItem
import com.shayarishaala.shayarishaala.trending.TrendingShayariListScreen


@Composable
fun ShayariRouting(navHostController: NavHostController) {


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
        composable(ShayariRoutingItems.splashScreen.route)
        {
            SplashScreen(navHostController)
        }
        composable(ShayariRoutingItems.shayriListScreen.route + "/{title}")
        {
            val title = it.arguments?.getString("title")

            ShayariListItem(navHostController, title)
        }
        composable(ShayariRoutingItems.quoteListItem.route + "/{title}")
        {
            val title = it.arguments?.getString("title")

            QuoteListItem(navHostController, title)
        }
        composable(ShayariRoutingItems.finalShayriScreen.route + "/{item}") {
            val value = it.arguments?.getString("item").toString()
            FinalShayriView(value)
        }
        composable(ShayariRoutingItems.trendingShayariListScreen.route) {
            TrendingShayariListScreen(navHostController)
        }
    }
}