package com.shayarishaala.shayarishaala.commonUI

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shayarishaala.shayarishaala.ui.theme.Pink80
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun FinalShayriView(finalShayari: String) {
    Surface {
        val context = LocalContext.current
        val clipboardManager: ClipboardManager = LocalClipboardManager.current
        var liked by remember { mutableStateOf(false) }
        var copied by remember { mutableStateOf(false) }
        var likeCount by remember { mutableStateOf(Random.nextInt(0, 200)) }

        // For heart animation
        val scope = rememberCoroutineScope()
        val heartScale = remember { Animatable(0f) }
        var showHeart by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Pink80)
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
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium)
                        )
                    }
                }

                // Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(150.dp, 82.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Like button card
                        Box(
                            modifier = Modifier
                                .padding(6.dp)
                                .size(100.dp, 46.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable {
                                        liked = !liked
                                        if (liked) {
                                            likeCount += 1
                                            showHeart = true
                                            scope.launch {
                                                heartScale.snapTo(0f)
                                                heartScale.animateTo(1.5f, tween(300))
                                                heartScale.animateTo(0f, tween(300))
                                                showHeart = false
                                            }
                                        } else {
                                            if (likeCount > 0) likeCount -= 1
                                        }
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(100.dp),
                                border = BorderStroke(1.dp, Color.LightGray)
                            ) {
                                // Center the icon and count vertically
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Like",
                                        tint = Color.Red,
                                        modifier = Modifier.size(30.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "$likeCount",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                    )
                                }
                            }

                            // ❤️ Big Heart Animation (overlay)
                            if (showHeart) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = Color.Red.copy(alpha = 0.4f),
                                    modifier = Modifier
                                        .size(500.dp)
                                        .scale(heartScale.value)
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
                                    putExtra(Intent.EXTRA_TEXT, finalShayari)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, null))
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
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
                                Toast.makeText(context, "Text Copied Successfully ✅", Toast.LENGTH_SHORT).show()

                                // Reset copied state after 1.5 seconds
                                Handler(Looper.getMainLooper()).postDelayed({
                                    copied = false
                                }, 1500)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (copied) Color(0xFFB9FBC0) else Color.White
                        ),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (copied) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Copied",
                                    tint = Color(0xFF00C853), // Green check color
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