package com.shayarishaala.shayarishaala.ui.kalam

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.shayarishaala.shayarishaala.BuildConfig
import com.shayarishaala.shayarishaala.Model.Routing.ShayariRoutingItems
import com.shayarishaala.shayarishaala.data.remote.GeminiService
import com.shayarishaala.shayarishaala.data.repository.KalamRepository
import com.shayarishaala.shayarishaala.favorites.FavoriteViewModel
import com.shayarishaala.shayarishaala.ui.components.ActionButton
import com.shayarishaala.shayarishaala.ui.components.ActionStyle
import com.shayarishaala.shayarishaala.ui.components.BentoTile
import com.shayarishaala.shayarishaala.ui.components.CopyButton
import com.shayarishaala.shayarishaala.ui.components.HeartButton
import com.shayarishaala.shayarishaala.ui.components.KaagazTopBar
import com.shayarishaala.shayarishaala.ui.components.MoodChip
import com.shayarishaala.shayarishaala.ui.components.NotebookCard
import com.shayarishaala.shayarishaala.ui.components.PillButton
import com.shayarishaala.shayarishaala.ui.components.pressClick
import com.shayarishaala.shayarishaala.ui.theme.BentoBlack
import com.shayarishaala.shayarishaala.ui.theme.Ink
import com.shayarishaala.shayarishaala.ui.theme.InkMuted
import com.shayarishaala.shayarishaala.ui.theme.Lilac
import com.shayarishaala.shayarishaala.ui.theme.Lime
import com.shayarishaala.shayarishaala.ui.theme.Motion
import com.shayarishaala.shayarishaala.ui.theme.Paper
import com.shayarishaala.shayarishaala.ui.theme.PaperCard
import com.shayarishaala.shayarishaala.ui.theme.ShayariTextStyle
import com.shayarishaala.shayarishaala.ui.theme.Sindoor
import com.shayarishaala.shayarishaala.ui.theme.moodColor
import com.shayarishaala.shayarishaala.utils.PreferenceManager
import com.shayarishaala.shayarishaala.utils.ShareUtils
import kotlinx.coroutines.delay

private const val DAILY_LIMIT = 5
private val QUICK_TOPICS = listOf("इश्क़", "दोस्ती", "बारिश", "माँ", "जुदाई", "हौसला")

@Composable
fun KalamScreen(navHostController: NavHostController) {
    val context = LocalContext.current

    // Initialize dependencies once
    val preferenceManager = remember { PreferenceManager(context) }

    val geminiService = remember {
        GeminiService(BuildConfig.GEMINI_API_KEY)
    }

    val repository = remember {
        KalamRepository(geminiService)
    }

    val viewModel: KalamViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return KalamViewModel(repository, preferenceManager) as T
            }
        }
    )

    val state by viewModel.state.collectAsState()
    val favoriteViewModel: FavoriteViewModel = viewModel()
    val isFavorite by favoriteViewModel.isFavorite(state.shayari)
        .collectAsStateWithLifecycle(initialValue = false)

    KalamScreenContent(
        state = state,
        isFavorite = isFavorite,
        onPromptChanged = { viewModel.onPromptChanged(it) },
        onGenerateClick = { viewModel.generateShayari() },
        onRegenerate = { viewModel.regenerate() },
        onShareClick = { ShareUtils.shareText(context, state.shayari) },
        onFavoriteToggle = { favoriteViewModel.toggleFavorite(state.shayari) },
        onBackClick = dropUnlessResumed { navHostController.popBackStack() },
        onClearError = { viewModel.clearError() },
        onOpenStudio = {
            navHostController.navigate(ShayariRoutingItems.studioScreen.withArg(state.shayari))
        }
    )
}

@Composable
fun KalamScreenContent(
    state: KalamUiState,
    isFavorite: Boolean,
    onPromptChanged: (String) -> Unit,
    onGenerateClick: () -> Unit,
    onRegenerate: () -> Unit,
    onShareClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onBackClick: () -> Unit,
    onClearError: () -> Unit,
    onOpenStudio: () -> Unit
) {
    val limitReached = state.generationCount >= DAILY_LIMIT

    fun editPrompt(text: String) {
        // The prompt is locked while Kalam is writing
        if (state.isLoading) return
        if (state.error != null) onClearError()
        onPromptChanged(text)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        KaagazTopBar(
            title = "कलम",
            subtitle = "Kalam AI · आपके शब्द, आपकी शायरी",
            onBack = onBackClick
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BentoTile(color = Lilac, onClick = null, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("आज क्या लिखें?", style = MaterialTheme.typography.headlineSmall, color = BentoBlack)
                        Text(
                            "कोई विषय, एहसास या पल लिखिए — कलम उसे 2-4 पंक्तियों की शायरी बना देगी।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ink
                        )
                    }
                    Spacer(Modifier.size(12.dp))
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(BentoBlack),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, tint = PaperCard)
                    }
                }
                Spacer(Modifier.height(16.dp))
                UsageMeter(used = state.generationCount, limit = DAILY_LIMIT)
            }

            NotebookCard(lineSpacing = 32.sp, firstRuleOffset = 74.dp) {
                Text("आपका विषय", style = MaterialTheme.typography.labelMedium, color = InkMuted)
                Spacer(Modifier.height(4.dp))
                BasicTextField(
                    value = state.userPrompt,
                    onValueChange = ::editPrompt,
                    textStyle = ShayariTextStyle.copy(fontSize = 19.sp, lineHeight = 32.sp),
                    cursorBrush = SolidColor(Sindoor),
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        Box {
                            if (state.userPrompt.isEmpty()) {
                                Text(
                                    "जैसे: बारिश में पहली मुलाक़ात…",
                                    style = ShayariTextStyle.copy(fontSize = 19.sp, lineHeight = 32.sp, color = InkMuted.copy(alpha = 0.6f))
                                )
                            }
                            inner()
                        }
                    }
                )
            }

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QUICK_TOPICS.forEachIndexed { index, topic ->
                    MoodChip(
                        text = topic,
                        color = moodColor(index),
                        modifier = Modifier.pressClick(onClickLabel = "Use topic $topic") {
                            editPrompt("$topic पर शायरी")
                        }
                    )
                }
            }

            PillButton(
                text = if (limitReached) "आज की सीमा पूरी" else "शायरी लिखो",
                icon = Icons.Outlined.AutoAwesome,
                onClick = onGenerateClick,
                enabled = !state.isLoading && !limitReached,
                loading = state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(
                visible = state.error != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                ErrorNote(message = state.error.orEmpty())
            }

            AnimatedVisibility(
                visible = state.isLoading,
                enter = expandVertically(tween(Motion.MEDIUM)) + fadeIn(),
                exit = shrinkVertically(tween(Motion.SHORT)) + fadeOut()
            ) {
                WritingLoader()
            }

            AnimatedVisibility(
                visible = state.shayari.isNotEmpty() && !state.isLoading,
                enter = expandVertically(tween(Motion.LONG, easing = Motion.EmphasizedDecelerate)) + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                ResultSection(
                    shayari = state.shayari,
                    isFavorite = isFavorite,
                    onFavoriteToggle = onFavoriteToggle,
                    onRegenerate = onRegenerate,
                    onOpenStudio = onOpenStudio,
                    onShareClick = onShareClick
                )
            }
        }
    }
}

/** Five dots, one per daily generation; used dots fill in with a spring. */
@Composable
private fun UsageMeter(used: Int, limit: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(limit) { i ->
            val filled = i < used
            val color by animateColorAsState(if (filled) BentoBlack else PaperCard, label = "usageDot")
            val scale = remember { Animatable(1f) }
            LaunchedEffect(filled) {
                if (filled) {
                    scale.snapTo(0.5f)
                    scale.animateTo(1f, spring(dampingRatio = 0.4f))
                }
            }
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    }
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
        Spacer(Modifier.size(4.dp))
        Text(
            text = "${(limit - used).coerceAtLeast(0)} / $limit आज बाकी",
            style = MaterialTheme.typography.labelMedium,
            color = Ink
        )
    }
}

/** A pen nib gliding over ruled lines while Gemini writes. */
@Composable
private fun WritingLoader() {
    val transition = rememberInfiniteTransition(label = "writing")
    val travel by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
        label = "travel"
    )
    val wobble by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(180, easing = LinearEasing), RepeatMode.Reverse),
        label = "wobble"
    )
    val shimmer by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "shimmer"
    )
    NotebookCard(lineSpacing = 32.sp, firstRuleOffset = 52.dp) {
        Text("कलम लिख रही है…", style = MaterialTheme.typography.labelLarge, color = InkMuted)
        Spacer(Modifier.height(14.dp))
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val lineWidths = listOf(0.92f, 0.75f, 0.84f)
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                lineWidths.forEachIndexed { line, width ->
                    // Each line "fills" as the pen passes over it
                    val reveal = ((travel * 3f) - line).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(width * reveal.coerceAtLeast(0.05f))
                            .height(12.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Ink.copy(alpha = 0.08f + 0.2f * shimmer))
                    )
                }
            }
            val activeLine = (travel * 3f).toInt().coerceIn(0, 2)
            val lineProgress = (travel * 3f) - activeLine
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                tint = Sindoor,
                modifier = Modifier
                    .offset(
                        x = maxWidth * lineWidths[activeLine] * lineProgress - 6.dp,
                        y = (32 * activeLine).dp - 14.dp + wobble.dp
                    )
                    .size(22.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
    }
}

/** Error shown as a sindoor note that shakes once when it appears. */
@Composable
private fun ErrorNote(message: String) {
    val shake = remember { Animatable(0f) }
    LaunchedEffect(message) {
        if (message.isNotEmpty()) {
            for (x in listOf(10f, -8f, 6f, -4f, 0f)) {
                shake.animateTo(x, tween(60))
            }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { translationX = shake.value * density }
            .clip(RoundedCornerShape(20.dp))
            .background(Sindoor.copy(alpha = 0.1f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = Sindoor)
        Text(message, style = MaterialTheme.typography.bodyMedium, color = Sindoor)
    }
}

/** The generated shayari, revealed word by word, with its actions. */
@Composable
private fun ResultSection(
    shayari: String,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onRegenerate: () -> Unit,
    onOpenStudio: () -> Unit,
    onShareClick: () -> Unit,
) {
    val words = remember(shayari) { shayari.split(" ") }
    var revealedFor by rememberSaveable { mutableStateOf("") }
    var visibleWords by remember(shayari) {
        mutableIntStateOf(if (revealedFor == shayari) words.size else 0)
    }
    LaunchedEffect(shayari) {
        while (visibleWords < words.size) {
            delay(70)
            visibleWords++
        }
        revealedFor = shayari
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NotebookCard(showTape = true, lineSpacing = 36.sp, firstRuleOffset = 54.dp) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = words.take(visibleWords).joinToString(" "),
                style = ShayariTextStyle.copy(fontSize = 21.sp, lineHeight = 36.sp)
            )
            Text(
                text = "— कलम",
                style = ShayariTextStyle.copy(fontSize = 16.sp, lineHeight = 24.sp, color = InkMuted),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 6.dp)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CopyButton(text = shayari, style = ActionStyle.Cell, resetMillis = 2000L, modifier = Modifier.weight(1f))
            HeartButton(isFavorite = isFavorite, onToggle = onFavoriteToggle, style = ActionStyle.Cell, modifier = Modifier.weight(1f))
            ActionButton(Icons.Outlined.Refresh, "फिर से", onRegenerate, ActionStyle.Cell, Modifier.weight(1f))
            ActionButton(Icons.Outlined.Palette, "Studio", onOpenStudio, ActionStyle.Cell, Modifier.weight(1f), container = Lime)
        }
        PillButton(
            text = "Share",
            icon = Icons.Outlined.Share,
            onClick = onShareClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
