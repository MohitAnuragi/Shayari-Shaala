package com.shayarishaala.shayarishaala.ui.kalam

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.shayarishaala.shayarishaala.BuildConfig
import com.shayarishaala.shayarishaala.data.remote.GeminiService
import com.shayarishaala.shayarishaala.data.repository.KalamRepository
import com.shayarishaala.shayarishaala.ui.theme.Purple40
import com.shayarishaala.shayarishaala.utils.PreferenceManager

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

    KalamScreenContent(
        state = state,
        onPromptChanged = { viewModel.onPromptChanged(it) },
        onGenerateClick = { viewModel.generateShayari() },
        onRegenerate = { viewModel.regenerate() },
        onCopyClick = {
            clipboardManager.setText(AnnotatedString(state.shayari))
            Toast.makeText(context, "Copied Successfully", Toast.LENGTH_SHORT).show()
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
        onFavoriteToggle = { viewModel.toggleFavorite() },
        onBackClick = { navHostController.popBackStack() },
        onClearError = { viewModel.clearError() }
    )
}

@Composable
fun KalamScreenContent(
    state: KalamUiState,
    onPromptChanged: (String) -> Unit,
    onGenerateClick: () -> Unit,
    onRegenerate: () -> Unit,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onBackClick: () -> Unit,
    onClearError: () -> Unit
) {
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
                                tint = Purple40
                            )
                        }
                    }

                    Text(
                        text = "Kalam Assistant ✨",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 16.dp)
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
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.8f)
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
                                        color = Color.Gray.copy(alpha = 0.6f)
                                    )
                                }
                                innerTextField()
                            },
                            maxLines = 3,
                            singleLine = false
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Generation Count Info
                    Text(
                        text = "Generations today: ${state.generationCount}/5",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
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
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Purple40,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "✨ Create Shayari",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Purple40
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
                        colors = CardDefaults.cardColors(Color(0xFFFFEBEE)),
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
                        colors = CardDefaults.cardColors(Color.White),
                        shape = RoundedCornerShape(12.dp)
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
                                color = Color.Black,
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
                            icon = Icons.Default.ContentCopy,
                            label = "Copy",
                            onClick = { onCopyClick() },
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
                            icon = if (state.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            label = "Like",
                            onClick = { onFavoriteToggle() },
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
        colors = CardDefaults.cardColors(Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(8.dp)
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
                    tint = Purple40,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Purple40
                )
            }
        }
    }
}




