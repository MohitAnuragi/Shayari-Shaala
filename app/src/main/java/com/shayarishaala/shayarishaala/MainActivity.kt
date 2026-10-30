package com.shayarishaala.shayarishaala

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.shayarishaala.shayarishaala.Model.Routing.ShayariRouting
import com.shayarishaala.shayarishaala.data.repository.ContentRepository
import com.shayarishaala.shayarishaala.ui.theme.ShayariShaalaTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        // Parse the JSON content while the splash screen is showing
        lifecycleScope.launch { ContentRepository.preload(applicationContext) }
        // Light paper UI everywhere, so system bar icons are always dark
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent {
            ShayariShaalaTheme {
                val navHostController = rememberNavController()
                ShayariRouting(navHostController = navHostController)
            }
        }
    }
}
