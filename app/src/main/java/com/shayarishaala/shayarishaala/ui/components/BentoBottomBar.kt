package com.shayarishaala.shayarishaala.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.shayarishaala.shayarishaala.ui.theme.BentoBlack
import com.shayarishaala.shayarishaala.ui.theme.Lime
import com.shayarishaala.shayarishaala.ui.theme.Motion
import com.shayarishaala.shayarishaala.ui.theme.PaperCard

enum class BottomTab(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    Home("Home", Icons.Outlined.Home, Icons.Filled.Home),
    Shayari("Shayari", Icons.AutoMirrored.Outlined.MenuBook, Icons.AutoMirrored.Filled.MenuBook),
    Quotes("Quotes", Icons.Outlined.FormatQuote, Icons.Filled.FormatQuote),
    Saved("Saved", Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite),
}

private val TabWidth = 58.dp
private val TabHeight = 52.dp
private val TabGap = 4.dp

/** Black floating pill with a lime indicator that slides to the selected tab, plus the Kalam pen. */
@Composable
fun BentoBottomBar(
    selected: BottomTab?,
    onTabClick: (BottomTab) -> Unit,
    onKalamClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedIndex = selected?.ordinal ?: 0
    val indicatorOffset by animateDpAsState(
        targetValue = (TabWidth + TabGap) * selectedIndex,
        animationSpec = Motion.bouncySpring(),
        label = "tabIndicator"
    )
    val indicatorAlpha by animateFloatAsState(if (selected != null) 1f else 0f, label = "indicatorAlpha")

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .shadow(16.dp, RoundedCornerShape(50), spotColor = Color(0x66000000))
                .clip(RoundedCornerShape(50))
                .background(BentoBlack)
                .padding(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .alpha(indicatorAlpha)
                    .size(TabWidth, TabHeight)
                    .clip(RoundedCornerShape(50))
                    .background(Lime)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(TabGap)) {
                BottomTab.entries.forEach { tab ->
                    val isSelected = tab == selected
                    val tint by animateColorAsState(
                        if (isSelected) BentoBlack else PaperCard,
                        label = "tabTint"
                    )
                    Box(
                        modifier = Modifier
                            .size(TabWidth, TabHeight)
                            .pressClick(pressedScale = 0.88f, role = Role.Tab) { onTabClick(tab) }
                            .semantics {
                                contentDescription = tab.label
                                this.selected = isSelected
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) tab.selectedIcon else tab.icon,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .size(64.dp)
                .pressClick(pressedScale = 0.88f, onClick = onKalamClick)
                .semantics { contentDescription = "Write with Kalam AI" }
                .shadow(16.dp, CircleShape, spotColor = Color(0x66000000))
                .clip(CircleShape)
                .background(Lime),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Edit, contentDescription = null, tint = BentoBlack, modifier = Modifier.size(26.dp))
        }
    }
}
