package com.shayarishaala.shayarishaala.Model.Routing

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.shayarishaala.shayarishaala.commonUI.FinalShayriView
import com.shayarishaala.shayarishaala.commonUI.ShayariAndQuotes
import com.shayarishaala.shayarishaala.commonUI.SplashScreen
import com.shayarishaala.shayarishaala.favorites.FavoritesScreen
import com.shayarishaala.shayarishaala.quoteUI.QuoteListItem
import com.shayarishaala.shayarishaala.quoteUI.QuoteScreen
import com.shayarishaala.shayarishaala.shayariUI.Catagory
import com.shayarishaala.shayarishaala.shayariUI.ShayariListItem
import com.shayarishaala.shayarishaala.ui.components.BentoBottomBar
import com.shayarishaala.shayarishaala.ui.components.BottomTab
import com.shayarishaala.shayarishaala.ui.components.LocalNavAnimatedScope
import com.shayarishaala.shayarishaala.ui.components.LocalSharedTransitionScope
import com.shayarishaala.shayarishaala.ui.kalam.KalamScreen
import com.shayarishaala.shayarishaala.ui.studio.ShayariStudioScreen
import com.shayarishaala.shayarishaala.ui.theme.Motion
import com.shayarishaala.shayarishaala.ui.theme.Paper

private val homeRoute = ShayariRoutingItems.shayariAndQuotes.route
private val tabRootRoutes = setOf(
    homeRoute,
    ShayariRoutingItems.categoryScreen.route,
    ShayariRoutingItems.quoteScreen.route,
    ShayariRoutingItems.favoritesScreen.route,
)

/** Which bottom tab a route belongs to; null means the bar is hidden there. */
private fun tabFor(route: String?): BottomTab? = when {
    route == null -> null
    route == homeRoute -> BottomTab.Home
    route == ShayariRoutingItems.categoryScreen.route ||
        route.startsWith(ShayariRoutingItems.shayriListScreen.route + "/") -> BottomTab.Shayari
    route == ShayariRoutingItems.quoteScreen.route ||
        route.startsWith(ShayariRoutingItems.quoteListItem.route + "/") -> BottomTab.Quotes
    route == ShayariRoutingItems.favoritesScreen.route -> BottomTab.Saved
    else -> null
}

private fun rootRouteFor(tab: BottomTab): String = when (tab) {
    BottomTab.Home -> homeRoute
    BottomTab.Shayari -> ShayariRoutingItems.categoryScreen.route
    BottomTab.Quotes -> ShayariRoutingItems.quoteScreen.route
    BottomTab.Saved -> ShayariRoutingItems.favoritesScreen.route
}

/** Kalam and Studio rise up like a sheet. */
private fun isSheet(route: String?): Boolean =
    route == ShayariRoutingItems.kalamScreen.route ||
        route?.startsWith(ShayariRoutingItems.studioScreen.route) == true

private fun isReader(route: String?): Boolean =
    route?.startsWith(ShayariRoutingItems.finalShayriScreen.route) == true

private fun isTabSwitch(from: String?, to: String?): Boolean =
    from in tabRootRoutes && to in tabRootRoutes

private typealias NavScope = AnimatedContentTransitionScope<NavBackStackEntry>

private val NavScope.from get() = initialState.destination.route
private val NavScope.to get() = targetState.destination.route

private fun NavScope.enter(): EnterTransition = when {
    from == ShayariRoutingItems.splashScreen.route -> fadeIn(tween(Motion.LONG))
    isSheet(to) -> slideInVertically(tween(Motion.LONG, easing = Motion.EmphasizedDecelerate)) { it } +
        fadeIn(tween(Motion.MEDIUM))
    isReader(to) -> fadeIn(tween(Motion.MEDIUM, delayMillis = 60))
    isTabSwitch(from, to) -> fadeIn(tween(Motion.MEDIUM, delayMillis = 70)) +
        scaleIn(tween(Motion.MEDIUM, delayMillis = 70, easing = Motion.EmphasizedDecelerate), initialScale = 0.96f)
    else -> slideInHorizontally(tween(Motion.MEDIUM, easing = Motion.EmphasizedDecelerate)) { it / 5 } +
        fadeIn(tween(Motion.MEDIUM))
}

private fun NavScope.exit(): ExitTransition = when {
    isSheet(to) -> scaleOut(tween(Motion.LONG), targetScale = 0.94f) + fadeOut(tween(Motion.LONG))
    isReader(to) -> fadeOut(tween(Motion.MEDIUM))
    isTabSwitch(from, to) -> fadeOut(tween(Motion.SHORT, easing = Motion.EmphasizedAccelerate))
    else -> slideOutHorizontally(tween(Motion.MEDIUM, easing = Motion.EmphasizedAccelerate)) { -it / 5 } +
        fadeOut(tween(Motion.MEDIUM))
}

private fun NavScope.popEnter(): EnterTransition = when {
    isSheet(from) -> scaleIn(tween(Motion.LONG, easing = Motion.EmphasizedDecelerate), initialScale = 0.94f) +
        fadeIn(tween(Motion.MEDIUM))
    isReader(from) -> fadeIn(tween(Motion.MEDIUM))
    isTabSwitch(from, to) -> fadeIn(tween(Motion.MEDIUM, delayMillis = 70))
    else -> slideInHorizontally(tween(Motion.MEDIUM, easing = Motion.EmphasizedDecelerate)) { -it / 5 } +
        fadeIn(tween(Motion.MEDIUM))
}

private fun NavScope.popExit(): ExitTransition = when {
    isSheet(from) -> slideOutVertically(tween(Motion.MEDIUM, easing = Motion.EmphasizedAccelerate)) { it } +
        fadeOut(tween(Motion.MEDIUM))
    isReader(from) -> fadeOut(tween(Motion.MEDIUM))
    isTabSwitch(from, to) -> fadeOut(tween(Motion.SHORT))
    else -> slideOutHorizontally(tween(Motion.MEDIUM, easing = Motion.EmphasizedAccelerate)) { it / 5 } +
        fadeOut(tween(Motion.MEDIUM))
}

/** A destination that exposes its animation scope for shared-element transitions. */
private fun NavGraphBuilder.screen(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(route, arguments) { entry ->
        CompositionLocalProvider(LocalNavAnimatedScope provides this) {
            content(entry)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ShayariRouting(navHostController: NavHostController) {

    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selectedTab = tabFor(currentRoute)

    fun openTab(tab: BottomTab) {
        val root = rootRouteFor(tab)
        if (tab == selectedTab) {
            // Tapping the current tab returns to its first page
            navHostController.popBackStack(root, inclusive = false)
        } else {
            navHostController.navigate(root) {
                popUpTo(homeRoute) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    SharedTransitionLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
    ) {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            Box(modifier = Modifier.fillMaxSize()) {
                NavHost(
                    navController = navHostController,
                    startDestination = ShayariRoutingItems.splashScreen.route,
                    enterTransition = { enter() },
                    exitTransition = { exit() },
                    popEnterTransition = { popEnter() },
                    popExitTransition = { popExit() }
                ) {
                    screen(ShayariRoutingItems.splashScreen.route) {
                        SplashScreen(navHostController)
                    }
                    screen(homeRoute) {
                        ShayariAndQuotes(navHostController)
                    }
                    screen(ShayariRoutingItems.categoryScreen.route) {
                        Catagory(navHostController)
                    }
                    screen(ShayariRoutingItems.quoteScreen.route) {
                        QuoteScreen(navHostController)
                    }
                    screen(ShayariRoutingItems.shayriListScreen.route + "/{title}") {
                        ShayariListItem(navHostController, it.arguments?.getString("title"))
                    }
                    screen(ShayariRoutingItems.quoteListItem.route + "/{title}") {
                        QuoteListItem(navHostController, it.arguments?.getString("title"))
                    }
                    screen(
                        ShayariRoutingItems.finalShayriScreen.pattern,
                        arguments = listOf(
                            navArgument("item") { type = NavType.StringType },
                            navArgument("type") { type = NavType.StringType; nullable = true; defaultValue = null },
                            navArgument("title") { type = NavType.StringType; nullable = true; defaultValue = null },
                            navArgument("index") { type = NavType.IntType; defaultValue = -1 },
                        )
                    ) {
                        FinalShayriView(
                            finalShayari = it.arguments?.getString("item").orEmpty(),
                            navHostController = navHostController,
                            type = it.arguments?.getString("type"),
                            title = it.arguments?.getString("title"),
                            index = it.arguments?.getInt("index") ?: -1
                        )
                    }
                    screen(ShayariRoutingItems.kalamScreen.route) {
                        KalamScreen(navHostController = navHostController)
                    }
                    screen(ShayariRoutingItems.favoritesScreen.route) {
                        FavoritesScreen(navHostController)
                    }
                    screen(ShayariRoutingItems.studioScreen.route + "/{shayariText}") {
                        val text = it.arguments?.getString("shayariText") ?: ""
                        ShayariStudioScreen(navHostController = navHostController, shayariText = text)
                    }
                }

                AnimatedVisibility(
                    visible = selectedTab != null,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    enter = slideInVertically(tween(Motion.LONG, easing = Motion.EmphasizedDecelerate)) { it * 2 } +
                        fadeIn(tween(Motion.MEDIUM)),
                    exit = slideOutVertically(tween(Motion.MEDIUM, easing = Motion.EmphasizedAccelerate)) { it * 2 } +
                        fadeOut(tween(Motion.SHORT))
                ) {
                    BentoBottomBar(
                        selected = selectedTab,
                        onTabClick = ::openTab,
                        onKalamClick = {
                            navHostController.navigate(ShayariRoutingItems.kalamScreen.route) {
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(bottom = 14.dp)
                    )
                }
            }
        }
    }
}
