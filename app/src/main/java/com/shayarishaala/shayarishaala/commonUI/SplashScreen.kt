package com.shayarishaala.shayarishaala.commonUI

import android.os.Build.VERSION.SDK_INT
import android.os.Handler
import android.os.Looper
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.Coil.imageLoader
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.shayarishaala.shayarishaala.Model.Routing.ShayariRoutingItems
import com.shayarishaala.shayarishaala.R
import com.shayarishaala.shayarishaala.ui.theme.Purple40
import com.shayarishaala.shayarishaala.ui.theme.Purple80

@Composable
fun SplashScreen(navHostController: NavHostController) {

    // Animation states
    var startAnimation by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Scale animation for logo
    val scale = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.6f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = { OvershootInterpolator(2f).getInterpolation(it) }
        )
    )

    // Fade animation
    val alpha = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500)
    )
    // for gif loading
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    // Start animations and navigate after delay
    LaunchedEffect(Unit) {
        startAnimation = true
        Handler(Looper.getMainLooper()).postDelayed({
            navHostController.navigate(ShayariRoutingItems.shayariAndQuotes.route) {
                popUpTo(0)
            }
        }, 2500)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Purple40
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Logo Image with animation
            Image(
                painter = painterResource(id = R.drawable.shayarishaala),
                contentDescription = "Shayari Shaala Logo",
                modifier = Modifier
                    .size(170.dp) // Bigger logo size for better look
                    .scale(scale.value)
                    .alpha(alpha.value)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )

            // Circular Loader at bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "दिल से जुड़ी हर बात, सिर्फ यहाँ ❤\uFE0F...",
                        fontSize = 16.sp,
                        color = Color.Yellow,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(R.drawable.progress)
                            .build(),
                        imageLoader = imageLoader
                    )
                    Image(
                        painter = painter,
                        contentDescription = "Create Shayari",
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

        }
    }
}
