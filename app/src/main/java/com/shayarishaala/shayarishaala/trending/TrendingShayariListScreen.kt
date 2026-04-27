package com.shayarishaala.shayarishaala.trending

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.shayarishaala.shayarishaala.Model.Routing.ShayariRoutingItems
import com.shayarishaala.shayarishaala.data.getList
import com.shayarishaala.shayarishaala.ui.theme.Pink80
import com.shayarishaala.shayarishaala.ui.theme.Purple40

@Composable
fun TrendingShayariListScreen(
    navHostController: NavHostController? = null
) {
    Surface {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Purple40)
                .padding(top = 35.dp, bottom = 35.dp)
        ) {
            Column {
                // 🔹 Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier
                            .size(45.dp)
                            .clickable {
                                navHostController?.navigate("shayariAndQuote")
                            },
                        colors = CardDefaults.cardColors(Color.White),
                        shape = RoundedCornerShape(200.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "back",
                                tint = Color.Black
                            )
                        }
                    }

                    Text(
                        text = "नए साल की शायरी 💫",
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(start = 15.dp),
                        color = Color.Black
                    )
                }

                // 🔹 Filter & Display "NEW YEAR 2026" Shayari from the list
                val trendingCategory = trendingShayariList().find { it.title == "NEW YEAR 2026" }

                trendingCategory?.list?.let { list ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp)
                    ) {
                        items(list) { item ->
                            Card(
                                shape = RoundedCornerShape(15.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                                    .clickable {
                                        navHostController?.navigate(
                                            ShayariRoutingItems.finalShayriScreen.route + "/$item"
                                        )
                                    },
                                colors = CardDefaults.cardColors(containerColor = Pink80),
                                border = BorderStroke(width = 1.dp, color = Color.White)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 15.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp),
                                        textAlign = TextAlign.Center,
                                        style = TextStyle(
                                            color = Color.White,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = FontFamily.Serif
                                        )
                                    )
                                }
                            }
                        }
                    }
                } ?: run {
                    // If no matching title found
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "❌ कोई शायरी उपलब्ध नहीं है",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }
            }
        }
    }
}
