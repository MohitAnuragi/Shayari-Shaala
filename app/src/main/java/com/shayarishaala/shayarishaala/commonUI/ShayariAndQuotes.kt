package com.shayarishaala.shayarishaala.commonUI

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.shayarishaala.shayarishaala.R

import com.shayarishaala.shayarishaala.ui.theme.Purple40

@Composable
fun ShayariAndQuotes(navHostController: NavHostController?) {
    Surface {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Purple40)
                .padding(top = 35.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                navHostController?.navigate("splash")
                            },
                        colors = CardDefaults.cardColors(Color.White),
                        shape = RoundedCornerShape(200.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                        }
                    }
                    Text(
                        text = "What's Your Favorite?",
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 26.sp,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp, top = 60.dp, end = 5.dp, bottom = 30.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .height(120.dp)
                            .clickable {

                                navHostController?.navigate("category")
                            },
                        colors = CardDefaults.cardColors(Color.White),
                        shape = RoundedCornerShape(15.dp),
                        border = BorderStroke(width = 2.dp, color = Color.Black)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.shayari1),
                                contentDescription = "shayari",
                                modifier = Modifier
                                    .size(140.dp)
                                    .padding(start = 5.dp)
                                    .clip(RoundedCornerShape(30.dp))
                            )
                            Text(
                                modifier = Modifier.padding(start = 170.dp, top = 40.dp),
                                text = "SHAYARI",
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Serif,
                                fontSize = 26.sp
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp, vertical = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .height(120.dp)
                            .clickable {
                                navHostController?.navigate("quote")
                            },
                        colors = CardDefaults.cardColors(Color.White),
                        shape = RoundedCornerShape(15.dp),
                        border = BorderStroke(width = 2.dp, color = Color.Black)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.quote),
                                contentDescription = "quotes",
                                modifier = Modifier
                                    .size(140.dp)
                                    .rotate(180f)
                                    .padding(start = 5.dp)
                                    .clip(RoundedCornerShape(30.dp))
                            )
                            Text(
                                modifier = Modifier.padding(start = 170.dp, top = 37.dp),
                                text = "QUOTES",
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Serif,
                                fontSize = 26.sp
                            )

                        }
                    }
                }
                Spacer(modifier = Modifier.height(25.dp))
                TrendingShayariCard(navHostController)
            }
        }
    }
}

@Composable
fun TrendingShayariCard(navHostController: NavHostController? = null) {
    // Animation for "Trending" glow or scale effect
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .scale(scale)
            .clickable { navHostController?.navigate("trending") },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)), // soft peachy bg
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, Color.Black.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background image blur or overlay (optional)
            Image(
                painter = painterResource(id = R.drawable.newyear),
                contentDescription = "Trending Shayari",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x22000000)),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay for text visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xAA000000))
                        )
                    )
            )

            // Content text
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "🔥 TRENDING SHAYARI",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Serif,
                    style = TextStyle(
                        letterSpacing = 1.sp,
                        shadow = Shadow(Color.Black, offset = Offset(2f, 2f), blurRadius = 4f)
                    )
                )

                Text(
                    text = "2026 की नए साल की शायरी 💫",
                    color = Color(0xFFFFEB3B),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Serif
                )
            }
        }
    }
}