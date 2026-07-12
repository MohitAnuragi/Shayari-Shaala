package com.shayarishaala.shayarishaala.ui.studio

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatAlignRight
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.shayarishaala.shayarishaala.commonUI.AppTopBar
import com.shayarishaala.shayarishaala.ui.theme.Purple40
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// ═══════════════════════════════════════════════════════════════════
//  Theme constants — kept in sync with the rest of the app
// ═══════════════════════════════════════════════════════════════════
private val ActiveTabColor   = Purple40            // Color(0xFF8677B4)
private val InactiveTabColor = Color.White
private val InactiveBorder   = Color.Black.copy(alpha = 0.15f)
private val LabelActive      = Color.White
private val LabelInactive    = Color.Black.copy(alpha = 0.65f)
private val PanelBackground  = Color.White
private val SectionLabelColor = Purple40

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
    val rootView = LocalView.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var previewCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }

    // React to export result
    LaunchedEffect(state.exportResult) {
        state.exportResult?.let { result ->
            when (result) {
                is ExportResult.Success -> snackbarHostState.showSnackbar(result.message)
                is ExportResult.Failure -> snackbarHostState.showSnackbar(" ${result.message}")
            }
            viewModel.clearExportResult()
        }
    }

    // Capture helper — called both for download and share
    suspend fun capturePreviewBitmap(): Bitmap? {
        val coords = previewCoords ?: return null
        val windowPos = coords.positionInWindow()
        val size = coords.size
        val bounds = android.graphics.Rect(
            windowPos.x.toInt(),
            windowPos.y.toInt(),
            (windowPos.x + size.width).toInt(),
            (windowPos.y + size.height).toInt()
        )
        return withContext(Dispatchers.IO) {
            captureView(context as Activity, rootView, bounds)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        // Use app's Purple40 background — consistent with all other screens
        containerColor = Purple40
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Top Bar — shared AppTopBar matching KalamScreen/Home ─────────
            AppTopBar(
                title = " SHAYARI STUDIO",
                onBackClick = { navHostController.popBackStack() }
            )

            // ── Preview ─────────────────────────────────────────────────────
            // The preview box is fillMaxWidth + wrapContentHeight so text
            // is never clipped by a fixed aspect-ratio constraint. The bitmap
            // capture still scales to 1080×1080 in the export path.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                ShayariPreviewCard(
                    state = state,
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .wrapContentHeight()
                        .onGloballyPositioned { previewCoords = it }
                )
            }

            // ── Tool Tab Row ─────────────────────────────────────────────────
            StudioToolTabRow(
                activeTool = state.activeTool,
                onToolSelected = { viewModel.onToolSelected(it) }
            )

            // ── Picker Panel ─────────────────────────────────────────────────
            AnimatedVisibility(
                visible = state.activeTool != null,
                enter = expandVertically(tween(250)) + fadeIn(tween(200)),
                exit = shrinkVertically(tween(200)) + fadeOut(tween(150))
            ) {
                StudioPickerPanel(
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
                            // Scale to 1080×1080 for consistent export quality
                            val scaled = Bitmap.createScaledBitmap(bmp, 1080, 1080, true)
                            viewModel.saveImage(context, scaled)
                        }
                    },
                    onShareClick = {
                        scope.launch {
                            val bmp = capturePreviewBitmap() ?: return@launch
                            val scaled = Bitmap.createScaledBitmap(bmp, 1080, 1080, true)
                            val uri = viewModel.getShareUri(context, scaled) ?: return@launch
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "image/png"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "📱 Shayari Shaala App:\nhttps://play.google.com/store/apps/details?id=com.shayarishaala.shayarishaala"
                                )
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Shayari"))
                        }
                    }
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  Preview card — this is the composable that gets captured as PNG
// ═══════════════════════════════════════════════════════════════════
@Composable
fun ShayariPreviewCard(state: ShayariStudioUiState, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shadow(elevation = 24.dp, shape = RoundedCornerShape(20.dp), clip = false)
            .clip(RoundedCornerShape(20.dp))
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
//  Tool tab row — aligned to app theme (Purple40 bg, white cards)
// ═══════════════════════════════════════════════════════════════════
private data class ToolTabItem(
    val tool: StudioTool,
    val label: String,
    val icon: @Composable () -> Unit
)

@Composable
private fun StudioToolTabRow(
    activeTool: StudioTool?,
    onToolSelected: (StudioTool) -> Unit
) {
    val tabs = listOf(
        ToolTabItem(StudioTool.THEME, "Theme") { Text("🎨", fontSize = 20.sp) },
        ToolTabItem(StudioTool.FONT, "Font") { Icon(Icons.Default.TextFields, null, Modifier.size(22.dp)) },
        ToolTabItem(StudioTool.COLOR, "Color") { Icon(Icons.Default.Palette, null, Modifier.size(22.dp)) },
        ToolTabItem(StudioTool.ALIGNMENT, "Align") { Icon(Icons.Default.FormatAlignCenter, null, Modifier.size(22.dp)) },
        ToolTabItem(StudioTool.CARD_STYLE, "Style") { Text("🪟", fontSize = 20.sp) },
        ToolTabItem(StudioTool.DOWNLOAD, "Export") { Icon(Icons.Default.Download, null, Modifier.size(22.dp)) }
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            // Match app background — seamless continuation of Purple40 screen
            .background(Purple40)
            .padding(vertical = 10.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tabs) { tab ->
            val isActive = activeTool == tab.tool
            val scale by animateFloatAsState(
                targetValue = if (isActive) 1.05f else 1f,
                animationSpec = tween(150),
                label = "tab_scale"
            )

            Card(
                modifier = Modifier
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .clickable { onToolSelected(tab.tool) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isActive) ActiveTabColor else InactiveTabColor
                ),
                border = if (isActive) null else BorderStroke(1.dp, InactiveBorder)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    androidx.compose.runtime.CompositionLocalProvider(
                        androidx.compose.material3.LocalContentColor provides
                                if (isActive) LabelActive else LabelInactive
                    ) {
                        tab.icon()
                    }
                    Text(
                        text = tab.label,
                        fontSize = 10.sp,
                        color = if (isActive) LabelActive else LabelInactive,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  Picker panel — white background, consistent with app card surfaces
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun StudioPickerPanel(
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
            .background(PanelBackground)
            .padding(vertical = 16.dp)
    ) {
        when (state.activeTool) {
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
            null -> { /* hidden */ }
        }
    }
}

// ─────────────────────────────────────────────
//  Theme picker
// ─────────────────────────────────────────────
@Composable
private fun ThemePicker(
    selected: ShayariBackground,
    onSelected: (ShayariBackground) -> Unit
) {
    Column {
        PickerSectionLabel("🎨  Background Theme")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ShayariBackground.entries) { bg ->
                val isSelected = bg == selected
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onSelected(bg) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(bg.brush)
                            .then(
                                if (isSelected) Modifier.drawBehind {
                                    drawRoundRect(
                                        color = ActiveTabColor,
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                                    )
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(bg.emoji, fontSize = 22.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = bg.displayName,
                        fontSize = 9.sp,
                        color = LabelInactive,
                        textAlign = TextAlign.Center,
                        maxLines = 1
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
        PickerSectionLabel(" Font Style")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(ShayariFont.entries) { font ->
                val isSelected = font == selectedFont
                Card(
                    modifier = Modifier
                        .height(68.dp)
                        .width(80.dp)
                        .clickable { onFontSelected(font) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) ActiveTabColor else InactiveTabColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isSelected) null else BorderStroke(1.dp, InactiveBorder)
                ) {
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
                            fontSize = 16.sp,
                            color = if (isSelected) LabelActive else LabelInactive
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = font.displayName,
                            fontSize = 8.sp,
                            color = if (isSelected) LabelActive else Color.Black.copy(alpha = 0.4f),
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        PickerSectionLabel(" Text Size")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextSizeOption.entries.forEach { size ->
                val isSelected = size == selectedSize
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clickable { onSizeSelected(size) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) ActiveTabColor else InactiveTabColor
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = if (isSelected) null else BorderStroke(1.dp, InactiveBorder)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = size.displayName,
                            color = if (isSelected) LabelActive else LabelInactive,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
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
        PickerSectionLabel(" Text Color")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(TextColorOption.entries) { option ->
                val isSelected = option == selected
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onSelected(option) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(option.color)
                            .then(
                                if (isSelected) Modifier.drawBehind {
                                    drawCircle(
                                        color = ActiveTabColor,
                                        radius = size.minDimension / 2f + 3.dp.toPx(),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                                    )
                                } else Modifier
                            )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = option.displayName,
                        fontSize = 9.sp,
                        color = LabelInactive,
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
        PickerSectionLabel(" Text Alignment")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val alignItems = listOf(
                Triple(TextAlignOption.LEFT, Icons.Default.FormatAlignLeft, "Left"),
                Triple(TextAlignOption.CENTER, Icons.Default.FormatAlignCenter, "Center"),
                Triple(TextAlignOption.RIGHT, Icons.Default.FormatAlignRight, "Right")
            )
            alignItems.forEach { (option, icon, label) ->
                val isSelected = option == selected
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .clickable { onSelected(option) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) ActiveTabColor else InactiveTabColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isSelected) null else BorderStroke(1.dp, InactiveBorder)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) LabelActive else LabelInactive,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            color = if (isSelected) LabelActive else LabelInactive,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
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
        PickerSectionLabel(" Card Style")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(CardStyle.entries) { style ->
                val isSelected = style == selected
                Card(
                    modifier = Modifier
                        .size(80.dp)
                        .clickable { onSelected(style) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) ActiveTabColor else InactiveTabColor
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = if (isSelected) null else BorderStroke(1.dp, InactiveBorder)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(style.emoji, fontSize = 24.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = style.displayName,
                            fontSize = 9.sp,
                            color = if (isSelected) LabelActive else LabelInactive,
                            textAlign = TextAlign.Center,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PickerSectionLabel("⬇ Export Shayari")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Download button — primary style (Purple40 fill)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clickable(enabled = !isExporting) { onDownloadClick() },
                colors = CardDefaults.cardColors(
                    containerColor = if (isExporting) Color.LightGray else ActiveTabColor
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Download, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Text("Save Image", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Share button — secondary style (white fill, Purple40 border)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clickable(enabled = !isExporting) { onShareClick() },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, ActiveTabColor)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Share, null, tint = ActiveTabColor, modifier = Modifier.size(20.dp))
                        Text("Share", color = ActiveTabColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
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
        color = SectionLabelColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, bottom = 10.dp)
    )
}

// ═══════════════════════════════════════════════════════════════════
//  Capture utility — PixelCopy (API 26+) with canvas fallback
// ═══════════════════════════════════════════════════════════════════
private fun captureView(
    activity: Activity,
    view: View,
    bounds: android.graphics.Rect
): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888)
        var result: Bitmap? = null
        var done = false
        val lock = Object()

        PixelCopy.request(
            activity.window,
            bounds,
            bitmap,
            { copyResult ->
                result = if (copyResult == PixelCopy.SUCCESS) bitmap else null
                synchronized(lock) {
                    done = true
                    (lock as Object).notifyAll()
                }
            },
            Handler(Looper.getMainLooper())
        )

        synchronized(lock) {
            var waited = 0
            while (!done && waited < 3000) {
                lock.wait(100)
                waited += 100
            }
        }
        result ?: fallbackCapture(view, bounds)
    } else {
        fallbackCapture(view, bounds)
    }
}

private fun fallbackCapture(view: View, bounds: android.graphics.Rect): Bitmap {
    val full = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(full)
    view.draw(canvas)
    return Bitmap.createBitmap(
        full,
        bounds.left.coerceIn(0, full.width - 1),
        bounds.top.coerceIn(0, full.height - 1),
        bounds.width().coerceAtMost(full.width - bounds.left),
        bounds.height().coerceAtMost(full.height - bounds.top)
    )
}
