package com.shayarishaala.shayarishaala.ui.studio

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.shayarishaala.shayarishaala.ui.components.KaagazTopBar
import com.shayarishaala.shayarishaala.ui.components.PillButton
import com.shayarishaala.shayarishaala.ui.components.PillStyle
import com.shayarishaala.shayarishaala.ui.components.pressClick
import com.shayarishaala.shayarishaala.ui.theme.BentoBlack
import com.shayarishaala.shayarishaala.ui.theme.DashRule
import com.shayarishaala.shayarishaala.ui.theme.Ink
import com.shayarishaala.shayarishaala.ui.theme.InkMuted
import com.shayarishaala.shayarishaala.ui.theme.Lime
import com.shayarishaala.shayarishaala.ui.theme.Motion
import com.shayarishaala.shayarishaala.ui.theme.Paper
import com.shayarishaala.shayarishaala.ui.theme.PaperCard
import com.shayarishaala.shayarishaala.utils.ShareUtils
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// ═══════════════════════════════════════════════════════════════════
//  Entry composable — wires ViewModel and navigation
// ═══════════════════════════════════════════════════════════════════
@Composable
fun ShayariStudioScreen(navHostController: NavHostController, shayariText: String) {
    val viewModel: ShayariStudioViewModel = viewModel(
        factory = ShayariStudioViewModel.factory(shayariText)
    )
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // The preview card draws itself into this layer, which is what gets exported.
    val previewLayer = rememberGraphicsLayer()

    // React to export result
    LaunchedEffect(state.exportResult) {
        state.exportResult?.let { result ->
            when (result) {
                is ExportResult.Success -> snackbarHostState.showSnackbar(result.message)
                is ExportResult.Failure -> snackbarHostState.showSnackbar(result.message)
            }
            viewModel.clearExportResult()
        }
    }

    // Capture helper — called both for download and share
    suspend fun capturePreviewBitmap(): Bitmap? = runCatching {
        previewLayer.toImageBitmap()
            .asAndroidBitmap()
            // Hardware bitmaps can't be scaled or compressed; take a software copy
            .copy(Bitmap.Config.ARGB_8888, false)
    }.getOrNull()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState, Modifier.navigationBarsPadding()) },
        containerColor = Paper,
        contentWindowInsets = WindowInsets(0)
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .verticalScroll(rememberScrollState())
        ) {
            KaagazTopBar(
                title = "स्टूडियो",
                subtitle = "Studio · शायरी को कार्ड बनाएं",
                onBack = dropUnlessResumed { navHostController.popBackStack() }
            )

            // ── Preview ─────────────────────────────────────────────────────
            // The preview box is fillMaxWidth + wrapContentHeight so text
            // is never clipped by a fixed aspect-ratio constraint. The export
            // keeps this aspect ratio, scaled to 1080 px wide.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                ShayariPreviewCard(
                    state = state,
                    exportLayer = previewLayer,
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .wrapContentHeight()
                )
            }

            StudioToolTabRow(
                activeTool = state.activeTool,
                onToolSelected = { viewModel.onToolSelected(it) }
            )

            // Keep showing the last tool while the panel collapses, so it doesn't go blank mid-animation
            var lastTool by remember { mutableStateOf(state.activeTool) }
            if (state.activeTool != null) lastTool = state.activeTool

            AnimatedVisibility(
                visible = state.activeTool != null,
                enter = expandVertically(tween(Motion.MEDIUM, easing = Motion.EmphasizedDecelerate)) + fadeIn(tween(Motion.MEDIUM)),
                exit = shrinkVertically(tween(Motion.SHORT)) + fadeOut(tween(Motion.SHORT))
            ) {
                StudioPickerPanel(
                    tool = state.activeTool ?: lastTool,
                    state = state,
                    onBackgroundSelected = { viewModel.onBackgroundSelected(it) },
                    onFontSelected = { viewModel.onFontSelected(it) },
                    onColorSelected = { viewModel.onTextColorSelected(it) },
                    onAlignmentSelected = { viewModel.onAlignmentSelected(it) },
                    onTextSizeSelected = { viewModel.onTextSizeSelected(it) },
                    onCardStyleSelected = { viewModel.onCardStyleSelected(it) },
                    isExporting = state.isExporting,
                    onDownloadClick = {
                        scope.launch {
                            val bmp = capturePreviewBitmap() ?: return@launch
                            val scaled = bmp.scaleToWidth()
                            viewModel.saveImage(context, scaled)
                        }
                    },
                    onShareClick = {
                        scope.launch {
                            val bmp = capturePreviewBitmap() ?: return@launch
                            val scaled = bmp.scaleToWidth()
                            val uri = viewModel.getShareUri(context, scaled) ?: return@launch
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "image/png"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "📱 Shayari Shaala App:\n${ShareUtils.PLAY_STORE_URL}"
                                )
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Shayari"))
                        }
                    }
                )
            }

            Spacer(
                Modifier
                    .navigationBarsPadding()
                    .height(24.dp)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  Preview card — this is the composable that gets exported as PNG
// ═══════════════════════════════════════════════════════════════════
@Composable
fun ShayariPreviewCard(
    state: ShayariStudioUiState,
    modifier: Modifier = Modifier,
    exportLayer: GraphicsLayer? = null,
) {
    Box(
        modifier = modifier
            .shadow(elevation = 24.dp, shape = RoundedCornerShape(20.dp), clip = false)
            .clip(RoundedCornerShape(20.dp))
            // Recorded inside the clip: the export is the full square card, nothing from the screen around it
            .then(
                if (exportLayer != null) Modifier.drawWithContent {
                    exportLayer.record { this@drawWithContent.drawContent() }
                    drawLayer(exportLayer)
                } else Modifier
            )
            .background(state.selectedBackground.brush),
        contentAlignment = Alignment.Center
    ) {
        // ── Text container — each style wraps its content height so text is
        //    never clipped. Transparent uses no inner card decoration.
        //    All other styles add their visual decoration via Modifier but
        //    still wrapContentHeight() to grow with content.
        val cardModifier: Modifier = when (state.selectedCardStyle) {
            CardStyle.TRANSPARENT -> Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
                .padding(vertical = 32.dp)

            CardStyle.GLASS -> Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
                .padding(vertical = 24.dp)
                .background(
                    Color.White.copy(alpha = 0.18f),
                    RoundedCornerShape(16.dp)
                )
                .then(
                    Modifier.drawBehind {
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.35f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
                        )
                    }
                )
                .padding(20.dp)

            CardStyle.SHADOW -> Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
                .padding(vertical = 24.dp)
                .shadow(16.dp, RoundedCornerShape(16.dp))
                .background(Color(0xFF1A1A1A).copy(alpha = 0.75f), RoundedCornerShape(16.dp))
                .padding(20.dp)

            CardStyle.ROUNDED -> Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
                .padding(vertical = 24.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(32.dp))
                .padding(20.dp)

            CardStyle.LUXURY -> Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
                .padding(vertical = 24.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF2C2C2C).copy(alpha = 0.90f),
                            Color(0xFF1A1A1A).copy(alpha = 0.92f)
                        )
                    ),
                    RoundedCornerShape(12.dp)
                )
                .then(
                    Modifier.drawBehind {
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFFFFD700), Color(0xFFFFA500), Color(0xFFFFD700))
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                        )
                    }
                )
                .padding(20.dp)
        }

        Box(
            modifier = cardModifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.shayariText,
                color = state.selectedTextColor.color,
                fontSize = state.selectedTextSize.spValue.sp,
                fontFamily = state.selectedFont.fontFamily,
                fontWeight = state.selectedFont.previewWeight,
                textAlign = state.selectedAlignment.textAlign,
                lineHeight = (state.selectedTextSize.spValue * 1.55f).sp,
                softWrap = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Watermark — always present, pinned to bottom end
        Text(
            text = "Shayari Shaala",
            color = Color.White.copy(alpha = 0.40f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 10.dp, end = 10.dp)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
//  Tool tabs — black bento pill with a sliding lime indicator
// ═══════════════════════════════════════════════════════════════════
private data class ToolTabItem(
    val tool: StudioTool,
    val label: String,
    val icon: ImageVector
)

private val toolTabs = listOf(
    ToolTabItem(StudioTool.THEME, "Theme", Icons.Outlined.Wallpaper),
    ToolTabItem(StudioTool.FONT, "Font", Icons.Outlined.TextFields),
    ToolTabItem(StudioTool.COLOR, "Color", Icons.Outlined.Palette),
    ToolTabItem(StudioTool.ALIGNMENT, "Align", Icons.Filled.FormatAlignCenter),
    ToolTabItem(StudioTool.CARD_STYLE, "Style", Icons.Outlined.Layers),
    ToolTabItem(StudioTool.DOWNLOAD, "Export", Icons.Outlined.Download)
)

@Composable
private fun StudioToolTabRow(
    activeTool: StudioTool?,
    onToolSelected: (StudioTool) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(50))
            .background(BentoBlack)
            .padding(5.dp)
    ) {
        val tabWidth = maxWidth / toolTabs.size
        val activeIndex = toolTabs.indexOfFirst { it.tool == activeTool }
        val indicatorX by animateDpAsState(
            targetValue = tabWidth * activeIndex.coerceAtLeast(0),
            animationSpec = Motion.bouncySpring(),
            label = "toolIndicator"
        )
        if (activeIndex >= 0) {
            Box(
                modifier = Modifier
                    .offset(x = indicatorX)
                    .width(tabWidth)
                    .height(60.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Lime)
            )
        }
        Row {
            toolTabs.forEach { tab ->
                val isActive = activeTool == tab.tool
                val content by animateColorAsState(
                    if (isActive) BentoBlack else PaperCard,
                    label = "toolTint"
                )
                Column(
                    modifier = Modifier
                        .width(tabWidth)
                        .height(60.dp)
                        .pressClick(pressedScale = 0.9f, role = Role.Tab) { onToolSelected(tab.tool) }
                        .semantics { selected = isActive },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically)
                ) {
                    Icon(tab.icon, contentDescription = null, tint = content, modifier = Modifier.size(22.dp))
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = content,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  Picker panel — white rounded sheet, content slides between tools
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun StudioPickerPanel(
    tool: StudioTool?,
    state: ShayariStudioUiState,
    onBackgroundSelected: (ShayariBackground) -> Unit,
    onFontSelected: (ShayariFont) -> Unit,
    onColorSelected: (TextColorOption) -> Unit,
    onAlignmentSelected: (TextAlignOption) -> Unit,
    onTextSizeSelected: (TextSizeOption) -> Unit,
    onCardStyleSelected: (CardStyle) -> Unit,
    isExporting: Boolean,
    onDownloadClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(PaperCard)
            .padding(vertical = 16.dp)
    ) {
        AnimatedContent(
            targetState = tool,
            transitionSpec = {
                val forward = (targetState?.ordinal ?: 0) >= (initialState?.ordinal ?: 0)
                (slideInHorizontally(tween(Motion.MEDIUM, easing = Motion.EmphasizedDecelerate)) { if (forward) it / 4 else -it / 4 } +
                    fadeIn(tween(Motion.MEDIUM)))
                    .togetherWith(fadeOut(tween(Motion.SHORT)) + slideOutHorizontally(tween(Motion.SHORT)) { if (forward) -it / 6 else it / 6 })
            },
            label = "pickerContent"
        ) { shownTool ->
            when (shownTool) {
                StudioTool.THEME -> ThemePicker(
                    selected = state.selectedBackground,
                    onSelected = onBackgroundSelected
                )
                StudioTool.FONT -> FontPicker(
                    selectedFont = state.selectedFont,
                    selectedSize = state.selectedTextSize,
                    onFontSelected = onFontSelected,
                    onSizeSelected = onTextSizeSelected
                )
                StudioTool.COLOR -> ColorPicker(
                    selected = state.selectedTextColor,
                    onSelected = onColorSelected
                )
                StudioTool.ALIGNMENT -> AlignmentPicker(
                    selected = state.selectedAlignment,
                    onSelected = onAlignmentSelected
                )
                StudioTool.CARD_STYLE -> CardStylePicker(
                    selected = state.selectedCardStyle,
                    onSelected = onCardStyleSelected
                )
                StudioTool.DOWNLOAD -> ExportPanel(
                    isExporting = isExporting,
                    onDownloadClick = onDownloadClick,
                    onShareClick = onShareClick
                )
                null -> Spacer(Modifier.height(1.dp))
            }
        }
    }
}

/** Selectable box used by every picker: black when selected, paper with a hairline otherwise. */
@Composable
private fun ChoiceBox(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    content: @Composable (contentColor: Color) -> Unit
) {
    val container by animateColorAsState(if (selected) BentoBlack else Paper, label = "choiceFill")
    val contentColor by animateColorAsState(if (selected) PaperCard else Ink, label = "choiceContent")
    Box(
        modifier = modifier
            .pressClick(pressedScale = 0.93f) { onClick() }
            .semantics { this.selected = selected }
            .clip(shape)
            .background(container)
            .then(if (selected) Modifier else Modifier.border(1.dp, DashRule, shape)),
        contentAlignment = Alignment.Center
    ) {
        content(contentColor)
    }
}

/** Ring drawn around the selected swatch. */
private fun Modifier.selectionRing(selected: Boolean, shape: Shape, ringColor: Color = BentoBlack): Modifier =
    if (!selected) this else this.border(3.dp, ringColor, shape).padding(5.dp)

// ─────────────────────────────────────────────
//  Theme picker
// ─────────────────────────────────────────────
@Composable
private fun ThemePicker(
    selected: ShayariBackground,
    onSelected: (ShayariBackground) -> Unit
) {
    Column {
        PickerSectionLabel("Background")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ShayariBackground.entries) { bg ->
                val isSelected = bg == selected
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(64.dp)
                        .pressClick(pressedScale = 0.9f) { onSelected(bg) }
                        .semantics {
                            contentDescription = bg.displayName
                            this.selected = isSelected
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .selectionRing(isSelected, RoundedCornerShape(18.dp))
                            .clip(RoundedCornerShape(14.dp))
                            .background(bg.brush),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(bg.emoji, fontSize = 20.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = bg.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Ink else InkMuted,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Font picker
// ─────────────────────────────────────────────
@Composable
private fun FontPicker(
    selectedFont: ShayariFont,
    selectedSize: TextSizeOption,
    onFontSelected: (ShayariFont) -> Unit,
    onSizeSelected: (TextSizeOption) -> Unit
) {
    Column {
        PickerSectionLabel("Font")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(ShayariFont.entries) { font ->
                ChoiceBox(
                    selected = font == selectedFont,
                    onClick = { onFontSelected(font) },
                    modifier = Modifier
                        .height(76.dp)
                        .width(88.dp)
                ) { color ->
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "अ Aa",
                            fontFamily = font.fontFamily,
                            fontWeight = font.previewWeight,
                            fontSize = 18.sp,
                            color = color
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = font.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = color.copy(alpha = 0.75f),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        PickerSectionLabel("Text size")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextSizeOption.entries.forEach { size ->
                ChoiceBox(
                    selected = size == selectedSize,
                    onClick = { onSizeSelected(size) },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) { color ->
                    Text(
                        text = size.displayName,
                        color = color,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Color picker
// ─────────────────────────────────────────────
@Composable
private fun ColorPicker(
    selected: TextColorOption,
    onSelected: (TextColorOption) -> Unit
) {
    Column {
        PickerSectionLabel("Text colour")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(TextColorOption.entries) { option ->
                val isSelected = option == selected
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .pressClick(pressedScale = 0.88f) { onSelected(option) }
                        .semantics {
                            contentDescription = option.displayName
                            this.selected = isSelected
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .selectionRing(isSelected, CircleShape)
                            .clip(CircleShape)
                            .background(option.color)
                            .border(1.dp, DashRule, CircleShape)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = option.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Ink else InkMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Alignment picker
// ─────────────────────────────────────────────
@Composable
private fun AlignmentPicker(
    selected: TextAlignOption,
    onSelected: (TextAlignOption) -> Unit
) {
    Column {
        PickerSectionLabel("Alignment")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val alignItems = listOf(
                Triple(TextAlignOption.LEFT, Icons.AutoMirrored.Filled.FormatAlignLeft, "Left"),
                Triple(TextAlignOption.CENTER, Icons.Filled.FormatAlignCenter, "Center"),
                Triple(TextAlignOption.RIGHT, Icons.AutoMirrored.Filled.FormatAlignRight, "Right")
            )
            alignItems.forEach { (option, icon, label) ->
                ChoiceBox(
                    selected = option == selected,
                    onClick = { onSelected(option) },
                    modifier = Modifier
                        .weight(1f)
                        .height(68.dp)
                ) { color ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                        Text(text = label, style = MaterialTheme.typography.labelMedium, color = color)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Card style picker
// ─────────────────────────────────────────────
@Composable
private fun CardStylePicker(
    selected: CardStyle,
    onSelected: (CardStyle) -> Unit
) {
    Column {
        PickerSectionLabel("Card style")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(CardStyle.entries) { style ->
                ChoiceBox(
                    selected = style == selected,
                    onClick = { onSelected(style) },
                    modifier = Modifier.size(84.dp)
                ) { color ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(style.emoji, fontSize = 24.sp)
                        Text(
                            text = style.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Export panel
// ─────────────────────────────────────────────
@Composable
private fun ExportPanel(
    isExporting: Boolean,
    onDownloadClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        PickerSectionLabel("Export · 1080 px PNG")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PillButton(
                text = "Save Image",
                icon = Icons.Outlined.Download,
                onClick = onDownloadClick,
                enabled = !isExporting,
                loading = isExporting,
                modifier = Modifier.weight(1f)
            )
            PillButton(
                text = "Share",
                icon = Icons.Outlined.Share,
                onClick = onShareClick,
                enabled = !isExporting,
                style = PillStyle.Outline,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Shared picker section label
// ─────────────────────────────────────────────
@Composable
private fun PickerSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = InkMuted,
        modifier = Modifier.padding(start = 16.dp, bottom = 10.dp)
    )
}

private const val EXPORT_WIDTH_PX = 1080

/** Scales to [targetWidth] keeping the aspect ratio, and recycles the source if a copy was made. */
private fun Bitmap.scaleToWidth(targetWidth: Int = EXPORT_WIDTH_PX): Bitmap {
    val targetHeight = (height * targetWidth / width.toFloat()).roundToInt().coerceAtLeast(1)
    val scaled = Bitmap.createScaledBitmap(this, targetWidth, targetHeight, true)
    if (scaled !== this) recycle()
    return scaled
}
