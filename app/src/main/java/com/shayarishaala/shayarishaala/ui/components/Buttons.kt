package com.shayarishaala.shayarishaala.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shayarishaala.shayarishaala.ui.theme.BentoBlack
import com.shayarishaala.shayarishaala.ui.theme.BentoLine
import com.shayarishaala.shayarishaala.ui.theme.Ink
import com.shayarishaala.shayarishaala.ui.theme.Lime
import com.shayarishaala.shayarishaala.ui.theme.Motion
import com.shayarishaala.shayarishaala.ui.theme.PaperCard
import com.shayarishaala.shayarishaala.ui.theme.Sindoor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/** Round icon button: outlined ink on paper, or filled. */
@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    containerColor: Color = Color.Transparent,
    contentColor: Color = Ink,
    borderColor: Color? = Ink,
) {
    Box(
        modifier = modifier
            .size(size)
            .pressClick(pressedScale = 0.9f, onClick = onClick)
            .semantics { this.contentDescription = contentDescription }
            .clip(CircleShape)
            .background(containerColor)
            .then(if (borderColor != null) Modifier.border(1.5.dp, borderColor, CircleShape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
    }
}

enum class PillStyle(val container: Color, val content: Color, val border: Color?) {
    Black(BentoBlack, PaperCard, null),
    Lime(com.shayarishaala.shayarishaala.ui.theme.Lime, BentoBlack, null),
    Outline(Color.Transparent, Ink, Ink),
}

/** Chunky bento pill button. */
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    style: PillStyle = PillStyle.Black,
    enabled: Boolean = true,
    loading: Boolean = false,
    height: Dp = 56.dp,
) {
    // Loading keeps the full colour (it's busy, not unavailable); only a truly disabled pill fades
    val dimmed = !enabled && !loading
    val container by animateColorAsState(
        if (dimmed) style.container.copy(alpha = 0.35f) else style.container,
        label = "pillContainer"
    )
    Row(
        modifier = modifier
            .height(height)
            .pressClick(enabled = enabled && !loading, onClick = onClick)
            .clip(RoundedCornerShape(50))
            .background(container)
            .then(if (style.border != null) Modifier.border(1.5.dp, style.border, RoundedCornerShape(50)) else Modifier)
            .padding(horizontal = 22.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = style.content,
                strokeWidth = 2.dp
            )
        } else if (icon != null) {
            Icon(icon, contentDescription = null, tint = style.content, modifier = Modifier.size(20.dp))
        }
        Text(
            text = text,
            color = style.content.copy(alpha = if (dimmed) 0.6f else 1f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/** Rounded bento tile with press feedback. */
@Composable
fun BentoTile(
    color: Color,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(26.dp),
    contentPadding: Dp = 18.dp,
    onClickLabel: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .then(
                if (onClick != null) Modifier.pressClick(pressedScale = 0.97f, onClickLabel = onClickLabel, onClick = onClick)
                else Modifier
            )
            .clip(shape)
            .background(color)
            .padding(contentPadding),
        content = content
    )
}

/** Where an action button sits, which decides its look. */
enum class ActionStyle { Paper, OnBlack, Cell }

@Composable
private fun ActionFrame(
    label: String,
    style: ActionStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    container: Color? = null,
    content: @Composable () -> Unit,
) {
    when (style) {
        ActionStyle.Paper, ActionStyle.OnBlack -> Box(
            modifier = modifier
                .size(44.dp)
                .pressClick(pressedScale = 0.88f, onClick = onClick)
                .semantics { contentDescription = label }
                .clip(CircleShape)
                .background(container ?: Color.Transparent)
                .then(
                    if (style == ActionStyle.OnBlack) Modifier.border(1.dp, BentoLine, CircleShape)
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) { content() }

        ActionStyle.Cell -> Column(
            modifier = modifier
                .height(72.dp)
                .pressClick(pressedScale = 0.94f, onClick = onClick)
                .clip(RoundedCornerShape(22.dp))
                .background(container ?: PaperCard),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            content()
            Text(label, style = MaterialTheme.typography.labelMedium, color = Ink)
        }
    }
}

private fun ActionStyle.iconTint(): Color = if (this == ActionStyle.OnBlack) PaperCard else Ink

/** Generic action (icon + label for the Cell style). */
@Composable
fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    style: ActionStyle,
    modifier: Modifier = Modifier,
    tint: Color = style.iconTint(),
    container: Color? = null,
) {
    ActionFrame(label, style, onClick, modifier, container) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun ShareButton(onClick: () -> Unit, style: ActionStyle, modifier: Modifier = Modifier) {
    ActionButton(Icons.Outlined.Share, "Share", onClick, style, modifier)
}

/** Copies [text]; the icon morphs into a tick for [resetMillis]. */
@Composable
fun CopyButton(
    text: String,
    style: ActionStyle,
    modifier: Modifier = Modifier,
    resetMillis: Long = 1500L,
) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            delay(resetMillis)
            copied = false
        }
    }
    val container = if (copied) Lime else null
    ActionFrame(
        label = if (copied) "Copied" else "Copy",
        style = style,
        onClick = {
            clipboardManager.setText(AnnotatedString(text))
            copied = true
        },
        modifier = modifier,
        container = container
    ) {
        AnimatedContent(
            targetState = copied,
            transitionSpec = {
                (scaleIn(Motion.bouncySpring(), initialScale = 0.4f) + fadeIn(tween(Motion.SHORT)))
                    .togetherWith(scaleOut(tween(Motion.SHORT), targetScale = 0.4f) + fadeOut(tween(Motion.SHORT)))
            },
            label = "copyIcon"
        ) { isCopied ->
            Icon(
                imageVector = if (isCopied) Icons.Filled.Check else Icons.Outlined.ContentCopy,
                contentDescription = null,
                tint = if (isCopied) BentoBlack else style.iconTint(),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/** Favourite toggle: the heart pops with a spring and a ring of ink dots bursts out when saving. */
@Composable
fun HeartButton(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    style: ActionStyle,
    modifier: Modifier = Modifier,
    activeColor: Color = if (style == ActionStyle.OnBlack) Color(0xFFFF8A8A) else Sindoor,
) {
    val scope = rememberCoroutineScope()
    val heartScale = remember { Animatable(1f) }
    val burst = remember { Animatable(0f) }
    val tint by animateColorAsState(
        if (isFavorite) activeColor else style.iconTint(),
        label = "heartTint"
    )
    ActionFrame(
        label = if (isFavorite) "Saved" else "Save",
        style = style,
        onClick = {
            val saving = !isFavorite
            onToggle()
            scope.launch {
                heartScale.snapTo(0.6f)
                heartScale.animateTo(1f, Motion.bouncySpring())
            }
            if (saving) {
                scope.launch {
                    burst.snapTo(0f)
                    burst.animateTo(1f, tween(Motion.LONG, easing = FastOutSlowInEasing))
                    burst.snapTo(0f)
                }
            }
        },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .drawBehind {
                    val p = burst.value
                    if (p > 0f) {
                        val radius = size.minDimension * (0.6f + 0.9f * p)
                        repeat(8) { i ->
                            val angle = Math.toRadians(i * 45.0 - 90.0)
                            drawCircle(
                                color = activeColor.copy(alpha = 1f - p),
                                radius = 2.5.dp.toPx() * (1f - p * 0.5f),
                                center = center + Offset(
                                    (cos(angle) * radius).toFloat(),
                                    (sin(angle) * radius).toFloat()
                                )
                            )
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                tint = tint,
                modifier = Modifier
                    .size(20.dp)
                    .scale(heartScale.value)
            )
        }
    }
}

/** Lime circle count badge used next to titles. */
@Composable
fun CountBadge(count: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Lime)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(count.toString(), style = MaterialTheme.typography.labelLarge, color = BentoBlack)
    }
}
