package com.shayarishaala.shayarishaala.ui.kalam

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.shayarishaala.shayarishaala.BuildConfig
import com.shayarishaala.shayarishaala.R
import com.shayarishaala.shayarishaala.data.remote.GeminiService
import com.shayarishaala.shayarishaala.data.repository.KalamRepository
import com.shayarishaala.shayarishaala.favorites.FavoriteViewModel
import com.shayarishaala.shayarishaala.ui.theme.Purple40
import com.shayarishaala.shayarishaala.utils.PreferenceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun KalamScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

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

    // For star burst animation
    val scope = rememberCoroutineScope()
    val starScale = remember { Animatable(0f) }
    var showStarBurst by remember { mutableStateOf(false) }

    KalamScreenContent(
        state = state,
        isFavorite = isFavorite,
        onPromptChanged = { viewModel.onPromptChanged(it) },
        onGenerateClick = { viewModel.generateShayari() },
        onRegenerate = { viewModel.regenerate() },
        onCopyClick = {
            clipboardManager.setText(AnnotatedString(state.shayari))
        },
        onShareClick = {
            val shareText = "❤️ Shayari:\n\n${state.shayari}\n\n📱 Shayari Shaala App:\nhttps://play.google.com/store/apps/details?id=com.shayarishaala.shayarishaala"
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Shayari"))
        },
        onFavoriteToggle = {
            favoriteViewModel.toggleFavorite(state.shayari)
            if (!isFavorite) {
                // animate only when saving
                showStarBurst = true
                scope.launch {
                    starScale.snapTo(0f)
                    starScale.animateTo(1.5f, tween(300))
                    starScale.animateTo(0f, tween(300))
                    showStarBurst = false
                }
            }
        },
        onBackClick = { navHostController.popBackStack() },
        onClearError = { viewModel.clearError() }
    )
}

@Composable
fun KalamScreenContent(
    state: KalamUiState,
    isFavorite: Boolean,
    onPromptChanged: (String) -> Unit,
    onGenerateClick: () -> Unit,
    onRegenerate: () -> Unit,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onBackClick: () -> Unit,
    onClearError: () -> Unit
) {
    var copied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    Surface {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Purple40)
                .padding(top = 25.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Bar
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Card(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { onBackClick() },
                        colors = CardDefaults.cardColors(Color.White),
                        shape = RoundedCornerShape(200.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    }

                    Text(
                        text = "Kalam Assistant ✨",
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 28.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Input Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "Your Creative Prompt",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // TextField
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(Color.White.copy(alpha = 0.95f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        BasicTextField(
                            value = state.userPrompt,
                            onValueChange = { onPromptChanged(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                color = Color.Black
                            ),
                            decorationBox = { innerTextField ->
                                if (state.userPrompt.isEmpty()) {
                                    Text(
                                        text = "Write a sad shayari about love...",
                                        fontSize = 14.sp,
                                        color = Color.Gray.copy(alpha = 0.4f)
                                    )
                                }
                                innerTextField()
                            },
                            maxLines = 5,
                            singleLine = false
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Generation Count Info
                    Text(
                        text = "Generations today: ${state.generationCount}/5",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Generate Button
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable(enabled = !state.isLoading && state.generationCount < 5) {
                                if (state.error != null) {
                                    onClearError()
                                } else {
                                    onGenerateClick()
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (state.generationCount >= 5 && state.shayari.isEmpty()) {
                                Color.Gray.copy(alpha = 0.5f)
                            } else {
                                Color.White
                            }
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (state.isLoading) {
                                val painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(context)
                                        .data(R.drawable.writtingpen)
                                        .build(),
                                    imageLoader = imageLoader
                                )
                                Image(
                                    painter = painter,
                                    contentDescription = "Create Shayari",
                                    modifier = Modifier.size(46.dp)
                                )
                            } else {
                                Text(
                                    text = "✨ Create Shayari",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Error Message
                if (state.error != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        colors = CardDefaults.cardColors(Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = state.error,
                            fontSize = 14.sp,
                            color = Color(0xFFD32F2F),
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Output Section
                if (state.shayari.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        colors = CardDefaults.cardColors(Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(width = 2.dp, color = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.shayari,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.Serif,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                lineHeight = 28.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Copy Button
                        ActionButton(
                            icon = if (copied) Icons.Default.Check else Icons.Outlined.ContentCopy,
                            label = "Copy",
                            onClick = {
                                onCopyClick()
                                copied = true
                                scope.launch {
                                    delay(2000)
                                    copied = false
                                }
                                      },
                            modifier = Modifier.weight(1f)
                        )

                        // Share Button
                        ActionButton(
                            icon = Icons.Default.Share,
                            label = "Share",
                            onClick = { onShareClick() },
                            modifier = Modifier.weight(1f)
                        )

                        // Favorite Button
                        ActionButton(
                            icon = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarOutline,
                            label = "Like",
                            onClick = {
                                onFavoriteToggle()

                                      },
                            modifier = Modifier.weight(1f)
                        )

                        // Regenerate Button
                        ActionButton(
                            icon = Icons.Default.Replay,
                            label = "Regen",
                            onClick = { onRegenerate() },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(44.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(0.6.dp, Color.Black)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
//                Text(
//                    text = label,
//                    fontSize = 10.sp,
//                    fontWeight = FontWeight.Medium,
//                    color = Purple40
//                )
            }
        }
    }
}




