package com.shayarishaala.shayarishaala.commonUI

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shayarishaala.shayarishaala.Model.Routing.ShayariRoutingItems
import com.shayarishaala.shayarishaala.R
import com.shayarishaala.shayarishaala.ui.theme.Ink
import com.shayarishaala.shayarishaala.ui.theme.InkMuted
import com.shayarishaala.shayarishaala.ui.theme.Motion
import com.shayarishaala.shayarishaala.ui.theme.Paper
import com.shayarishaala.shayarishaala.ui.theme.Sindoor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navHostController: NavHostController) {
    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    val writeProgress = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            logoScale.animateTo(1f, tween(1000, easing = { OvershootInterpolator(2f).getInterpolation(it) }))
        }
        launch { logoAlpha.animateTo(1f, tween(800)) }
        launch {
            delay(450)
            // The title is "written" left to right, like ink from a pen
            writeProgress.animateTo(1f, tween(1100, easing = Motion.EmphasizedDecelerate))
        }
        launch {
            delay(1300)
            taglineAlpha.animateTo(1f, tween(Motion.LONG))
        }
        delay(2500)
        navHostController.navigate(ShayariRoutingItems.shayariAndQuotes.route) {
            popUpTo(ShayariRoutingItems.splashScreen.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.shayarishaala),
                contentDescription = "Shayari Shaala logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .graphicsLayer {
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                        alpha = logoAlpha.value
                        rotationZ = -3f * (1f - logoScale.value)
                    }
                    .shadow(18.dp, RoundedCornerShape(32.dp))
                    .clip(RoundedCornerShape(32.dp))
            )
            Spacer(Modifier.height(28.dp))
            Text(
                text = "शायरी शाला",
                style = MaterialTheme.typography.displayMedium,
                color = Ink,
                modifier = Modifier.drawWithContent {
                    clipRect(
                        right = size.width * writeProgress.value,
                        clipOp = ClipOp.Intersect
                    ) { this@drawWithContent.drawContent() }
                    // A small ink dot rides the pen tip while writing
                    if (writeProgress.value in 0.01f..0.99f) {
                        drawCircle(
                            color = Sindoor,
                            radius = 4.dp.toPx(),
                            center = Offset(size.width * writeProgress.value, size.height * 0.62f)
                        )
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 56.dp)
                .graphicsLayer { alpha = taglineAlpha.value },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "दिल से जुड़ी हर बात, सिर्फ यहाँ",
                style = MaterialTheme.typography.titleMedium,
                color = InkMuted,
                textAlign = TextAlign.Center
            )
            InkDots()
        }
    }
}

/** Three ink drops that pulse in turn, a calm loading hint. */
@Composable
private fun InkDots() {
    val transition = rememberInfiniteTransition(label = "inkDots")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = "phase"
    )
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        repeat(3) { i ->
            val distance = kotlin.math.abs(phase - i - 0.5f).coerceAtMost(1.5f)
            val lift = (1f - distance / 1.5f).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationY = -6.dp.toPx() * lift
                        alpha = 0.35f + 0.65f * lift
                    }
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(Ink)
            )
        }
    }
}
