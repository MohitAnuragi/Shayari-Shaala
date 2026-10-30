package com.shayarishaala.shayarishaala.ui.components

import android.os.SystemClock
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shayarishaala.shayarishaala.ui.theme.Motion
import kotlinx.coroutines.delay

/**
 * Clickable that shrinks slightly while pressed and springs back, used for every tile and button
 * so the whole app shares one tactile feel.
 */
fun Modifier.pressClick(
    enabled: Boolean = true,
    pressedScale: Float = 0.96f,
    role: Role = Role.Button,
    onClickLabel: String? = null,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) pressedScale else 1f,
        animationSpec = Motion.pressSpring(),
        label = "pressScale"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            role = role,
            onClickLabel = onClickLabel,
            onClick = onClick
        )
}

/**
 * Remembers whether a screen already played its entrance, so cards only stagger in on the first
 * visit and not again when the user comes back or scrolls.
 */
@Stable
class EnterOnceState(val skip: Boolean) {
    internal val seen = mutableSetOf<Any>()
    internal val startedAt = SystemClock.uptimeMillis()
}

@Composable
fun rememberEnterOnceState(): EnterOnceState {
    var played by rememberSaveable { mutableStateOf(false) }
    val state = remember { EnterOnceState(skip = played) }
    LaunchedEffect(Unit) { played = true }
    return state
}

/** Fades and lifts an item into place, delayed by its position for a staggered cascade. */
fun Modifier.staggeredEnter(
    index: Int,
    state: EnterOnceState,
    key: Any = index,
    stepMillis: Long = 45L,
    rise: Dp = 24.dp
): Modifier = composed {
    val alreadyShown = state.skip || key in state.seen
    val progress = remember { Animatable(if (alreadyShown) 1f else 0f) }
    LaunchedEffect(Unit) {
        if (progress.value < 1f) {
            state.seen += key
            val elapsed = SystemClock.uptimeMillis() - state.startedAt
            val wait = (index * stepMillis - elapsed).coerceIn(0L, 450L)
            delay(wait)
            progress.animateTo(1f, tween(Motion.LONG, easing = Motion.EmphasizedDecelerate))
        }
    }
    graphicsLayer {
        alpha = progress.value
        translationY = (1f - progress.value) * rise.toPx()
    }
}

/** Dashed rule under a row, like a hand-ruled contents page. */
fun Modifier.dashedBottomRule(color: Color, strokeWidth: Dp = 1.dp): Modifier = drawBehind {
    val y = size.height - strokeWidth.toPx() / 2
    drawLine(
        color = color,
        start = Offset(0f, y),
        end = Offset(size.width, y),
        strokeWidth = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 5.dp.toPx()))
    )
}
