package com.shayarishaala.shayarishaala.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

/** Set by ShayariRouting around the NavHost. */
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }

/** Set by ShayariRouting inside every destination. */
val LocalNavAnimatedScope = staticCompositionLocalOf<AnimatedVisibilityScope?> { null }

/** Keys a shayari card shared between a list and the reader page. */
object SharedKeys {
    fun inCategory(type: String, title: String, index: Int) = "shayari|$type|$title|$index"
    fun single(text: String) = "shayari|single|$text"
}

/**
 * Lets a card morph into the matching reader page (and back). Does nothing when the shared
 * transition scopes are not provided, e.g. in previews.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.sharedShayari(key: String): Modifier = composed {
    val sharedScope = LocalSharedTransitionScope.current
    val animatedScope = LocalNavAnimatedScope.current
    if (sharedScope == null || animatedScope == null) {
        Modifier
    } else {
        with(sharedScope) {
            Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedScope
            )
        }
    }
}
