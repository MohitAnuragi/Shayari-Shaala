package com.shayarishaala.shayarishaala.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shayarishaala.shayarishaala.ui.theme.Ink
import com.shayarishaala.shayarishaala.ui.theme.InkMuted
import com.shayarishaala.shayarishaala.ui.theme.Lilac
import com.shayarishaala.shayarishaala.ui.theme.PaperCard
import com.shayarishaala.shayarishaala.ui.theme.RuleLine
import com.shayarishaala.shayarishaala.ui.theme.Tape

/** Space to leave under scrolling content on screens that show the floating bottom bar. */
val BottomBarSpace: Dp = 112.dp

/** Screen header: optional round back button, handwritten title, small subtitle. */
@Composable
fun KaagazTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            CircleIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onBack
            )
            Spacer(Modifier.width(14.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        actions()
    }
}

/**
 * A page torn from a ruled notebook: warm card, hand-ruled lines, soft shadow and optional tape.
 * [lineSpacing] should match the line height of the text inside so words sit on the rules.
 */
@Composable
fun NotebookCard(
    modifier: Modifier = Modifier,
    tilt: Float = 0f,
    showTape: Boolean = false,
    lineSpacing: TextUnit = 38.sp,
    firstRuleOffset: Dp = 50.dp,
    contentPadding: Dp = 20.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Box(modifier = modifier.graphicsLayer { rotationZ = tilt }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = shape,
                    ambientColor = Color(0x553C280A),
                    spotColor = Color(0x663C280A)
                )
                .clip(shape)
                .background(PaperCard)
                .drawBehind {
                    val step = lineSpacing.toPx()
                    var y = firstRuleOffset.toPx()
                    while (y < size.height - 4.dp.toPx()) {
                        drawLine(
                            color = RuleLine,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                        y += step
                    }
                }
                .padding(contentPadding),
            content = content
        )
        if (showTape) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-10).dp)
                    .rotate(2f)
                    .size(width = 90.dp, height = 22.dp)
                    .background(Tape)
            )
        }
    }
}

/** Friendly empty page with an icon in a lilac circle. */
@Composable
fun EmptyNotebook(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Lilac),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Ink, modifier = Modifier.size(44.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = Ink,
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = InkMuted,
            textAlign = TextAlign.Center
        )
    }
}
