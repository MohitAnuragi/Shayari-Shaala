package com.shayarishaala.shayarishaala.commonUI

import android.R.attr.category
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shayarishaala.shayarishaala.favorites.FavoriteViewModel
import com.shayarishaala.shayarishaala.ui.theme.Pink80
import com.shayarishaala.shayarishaala.ui.theme.Purple40
import kotlinx.coroutines.launch

@Composable
fun FinalShayriView(finalShayari: String) {
    Surface {
        val context = LocalContext.current
        val clipboardManager: ClipboardManager = LocalClipboardManager.current
        var copied by remember { mutableStateOf(false) }


        val favoriteViewModel: FavoriteViewModel = viewModel()
        val isFavorite by favoriteViewModel.isFavorite(finalShayari)
            .collectAsStateWithLifecycle(initialValue = false)

        // For star burst animation
        val scope = rememberCoroutineScope()
        val starScale = remember { Animatable(0f) }
        var showStarBurst by remember { mutableStateOf(false) }



        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Purple40)
                .padding(top = 35.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Shayari Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    border = BorderStroke(width = 3.dp, color = Color.White)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            text = finalShayari,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Serif)
                        )
                    }
                }

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // ⭐ Favorite Button (Room-backed)
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(150.dp, 82.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(6.dp)
                                .size(110.dp, 46.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable {
                                        favoriteViewModel.toggleFavorite(finalShayari)
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
                                shape = RoundedCornerShape(100.dp),
                                border = BorderStroke(1.dp, Color.LightGray)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                        contentDescription = "Favorite",
                                        tint = if (isFavorite) Color(0xFFFFB300) else Color.Black,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (isFavorite) "Saved" else "Save",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                    )
                                }
                            }

                            // Star Burst Animation (overlay)
                            if (showStarBurst) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700).copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .size(500.dp)
                                        .scale(starScale.value)
                                )
                            }
                        }
                    }

                    // 🔗 Share Button
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(50.dp, 42.dp)
                            .clickable {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "❤️ Shayari:\n\n$finalShayari\n\n📱 Shayari Shaala App:\nhttps://play.google.com/store/apps/details?id=com.shayarishaala.shayarishaala"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, null))
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "share")
                        }
                    }

                    // 📋 Copy Button
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(50.dp, 42.dp)
                            .clickable {
                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(finalShayari))
                                copied = true
                                Handler(Looper.getMainLooper()).postDelayed({
                                    copied = false
                                }, 1500)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (copied) Color(0xFFB9FBC0) else Color.White
                        ),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            if (copied) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Copied",
                                    tint = Color(0xFF16E069),
                                    modifier = Modifier.size(28.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = Color.Black,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}