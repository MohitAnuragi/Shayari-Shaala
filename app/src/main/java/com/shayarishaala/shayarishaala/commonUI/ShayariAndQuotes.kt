package com.shayarishaala.shayarishaala.commonUI

import android.os.Build.VERSION.SDK_INT
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.shayarishaala.shayarishaala.R
import com.shayarishaala.shayarishaala.daily.DailyShayariCard
import com.shayarishaala.shayarishaala.daily.DailyShayariRepository
import com.shayarishaala.shayarishaala.ui.theme.Purple40

@Composable
fun ShayariAndQuotes(navHostController: NavHostController?) {
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
//                    Card(
//                        modifier = Modifier
//                            .size(40.dp)
//                            .clickable {
//                                navHostController?.navigate("splash")
//                            },
//                        colors = CardDefaults.cardColors(Color.White),
//                        shape = RoundedCornerShape(200.dp)
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 9.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
//                        }
//                    }
                    Text(
                        text = "What's Your Favorite?",
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 26.sp,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .weight(1f)
                    )

                    // ⭐ Favorites Icon Button
                    Card(
                        modifier = Modifier
                            .size(42.dp)
                            .clickable {
                                navHostController?.navigate("favorites")
                            },
                        colors = CardDefaults.cardColors(Color.White),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(context)
                                    .data(R.drawable.favorite)
                                    .build(),
                                imageLoader = imageLoader
                            )

                            Image(
                                painter = painter,
                                contentDescription = "Create Shayari",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

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

                // Daily Shayari Feature
                val dailyShayari = remember { DailyShayariRepository.getTodayShayari() }
                DailyShayariCard(dailyShayari = dailyShayari)
            }
        }
    }
}
