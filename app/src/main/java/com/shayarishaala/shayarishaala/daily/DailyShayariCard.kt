package com.shayarishaala.shayarishaala.daily

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shayarishaala.shayarishaala.favorites.FavoriteViewModel
import com.shayarishaala.shayarishaala.ui.theme.Pink80
import com.shayarishaala.shayarishaala.utils.DailyShayariHelper
import com.shayarishaala.shayarishaala.utils.PreferenceManager
import kotlinx.coroutines.launch


@Composable
fun DailyShayariCard(dailyShayari: String? = null) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val shayari = dailyShayari ?: "आज की मेहनत ही कल की पहचान बनती है, इसलिए हर दिन को पूरी ईमानदारी से जियो और अपने सपनों को सच करने में लग जाओ।"
    val favoriteViewModel: FavoriteViewModel = viewModel()
    val isFavorite by favoriteViewModel.isFavorite(dailyShayari?: "आज की मेहनत ही कल की पहचान बनती है, इसलिए हर दिन को पूरी ईमानदारी से जियो और अपने सपनों को सच करने में लग जाओ।")
        .collectAsStateWithLifecycle(initialValue = false)

    // For star burst animation
    val scope = rememberCoroutineScope()
    val starScale = remember { Animatable(0f) }
    var showStarBurst by remember { mutableStateOf(false) }
    var copied by remember { mutableStateOf(false) }

    // Subtle scaling animation for premium feel
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 16.dp)
    ) {
        // Title
        Text(
            text = "🌟 Today's Shayari",
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Serif,
            fontSize = 26.sp,
            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp),
            color = Color.Black
        )

        // Premium Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .scale(scale),
            colors = CardDefaults.cardColors(
                containerColor = Pink80
            ),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(2.dp, Color.Black),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Pink80,
                                Color(0xFFD4D2E1)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Shayari Text (Center)
                    Text(
                        text = shayari,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 28.sp,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.3f),
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )

                    // Action Row (Bottom)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Copy Button
                        Card(
                            modifier = Modifier
                                .size(44.dp)
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(shayari))
                                    copied = true
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        copied = false
                                    }, 1500)
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (copied) Color(0xFFB9FBC0) else Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(0.6.dp, Color.Black)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                    if (copied) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Copied",
                                            tint = Color(0xFF16E069),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = Color(0xFF6200EE),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                            }
                        }

                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(28.dp))

                        // Share Button
                        Card(
                            modifier = Modifier
                                .size(44.dp)
                                .clickable {
                                    val shareText = "🌟 Today's Shayari:\n\n$shayari\n\n📱 Shayari Shaala App:\nhttps://play.google.com/store/apps/details?id=com.shayarishaala.shayarishaala"
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(
                                        Intent.createChooser(
                                            shareIntent,
                                            "Share Shayari"
                                        )
                                    )
                                },
                            colors = CardDefaults.cardColors(Color.White.copy(alpha = 0.9f)),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(0.6.dp, Color.Black)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Color(0xFF6200EE),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(28.dp))

                        // Favorite Button
                        Card(
                            modifier = Modifier
                                .size(44.dp)
                                .clickable {
                                    favoriteViewModel.toggleFavorite(dailyShayari?: "")
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
                            colors = CardDefaults.cardColors(
                                containerColor = if (isFavorite) Color(0xFFE5D383) else Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(0.6.dp, Color.Black)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                    contentDescription = "Favorite",
                                    tint = if (isFavorite) Color(0xFFFFB300) else Color(0xFF6200EE),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

