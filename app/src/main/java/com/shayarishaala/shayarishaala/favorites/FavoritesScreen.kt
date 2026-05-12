package com.shayarishaala.shayarishaala.favorites

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import com.shayarishaala.shayarishaala.Model.Routing.ShayariRoutingItems
import kotlin.collections.isNotEmpty

@Composable
fun FavoritesScreen(navHostController: NavHostController?) {
    val favoriteViewModel: FavoriteViewModel = viewModel()
    val favorites by favoriteViewModel.favoriteList.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val gradientBg = Brush.verticalGradient(
        colors = listOf(Color(0xFFA597DC), Color(0xFF82799F), Color(0xFF9B90C7))
    )

    Surface {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBg)
                .padding(top = 35.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    Card(
                        modifier = Modifier
                            .size(42.dp)
                            .clickable { navHostController?.popBackStack() },
                        colors = CardDefaults.cardColors(Color.White),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
//                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Your Favorites",
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 26.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Count badge
                    if (favorites.isNotEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(Color(0xFFE3CE61)),
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = "${favorites.size}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1A0033)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── Content ────────────────────────────────────────────────
                if (favorites.isEmpty()) {
                    // Empty State
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.4f),
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No favorites yet ❤️",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.7f),
                                fontFamily = FontFamily.Serif
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap ⭐ on any shayari to save it here",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.45f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 14.dp,
                            end = 14.dp,
                            top = 4.dp,
                            bottom = 100.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        itemsIndexed(favorites, key = { _, item -> item.id }) { index, fav ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically { it / 2 },
                                exit = fadeOut()
                            ) {
                                FavoriteCard(
                                    navHostController = navHostController,
                                    text = fav.text,
                                    category = fav.category,
                                    onRemove = { favoriteViewModel.removeFavorite(fav.text) },
                                    onCopy = {
                                        clipboardManager.setText(AnnotatedString(fav.text))
                                    },
                                    onShare = {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "❤️ Shayari:\n\n${fav.text}\n\n📱 Shayari Shaala App:\nhttps://play.google.com/store/apps/details?id=com.shayarishaala.shayarishaala"
                                            )
                                        }
                                        context.startActivity(
                                            Intent.createChooser(shareIntent, null)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteCard(
    navHostController: NavHostController?,
    text: String,
    category: String,
    onRemove: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,

) {
    var copied by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable {
            navHostController?.navigate(ShayariRoutingItems.finalShayriScreen.route + "/$text")
        },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color.Black),
        border = BorderStroke(1.5.dp, Color.White),

    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Category chip
            Card(
                colors = CardDefaults.cardColors(Color(0xFFEED045).copy(alpha = 0.18f)),
                shape = RoundedCornerShape(100.dp)
            ) {
                Text(
                    text = category,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Shayari Text
            Text(
                text = text,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp,
                    fontFamily = FontFamily.Serif
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.White)
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Copy
                ActionChip(

                    icon = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                    label = if (copied) "Copied!" else "Copy",
                    tint = if (copied) Color(0xFF15B768) else Color.Black,
                    onClick = {
                        onCopy()
                        copied = true
                        Handler(Looper.getMainLooper()).postDelayed({ copied = false }, 1500)
                    }
                )
                // Share
                ActionChip(
                    icon = Icons.Default.Share,
                    label = "Share",
                    tint = Color.Black,
                    onClick = onShare
                )
                // Remove
                ActionChip(
                    icon = Icons.Default.Star,
                    label = "Remove",
                    tint = Color.Black,
                    onClick = onRemove
                )
            }
        }
    }
}

@Composable
private fun ActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(100.dp),
        border = BorderStroke(0.2.dp, Color.Black)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(16.dp))
            Text(text = label, fontSize = 12.sp, color = tint, fontWeight = FontWeight.Medium)
        }
    }
}
