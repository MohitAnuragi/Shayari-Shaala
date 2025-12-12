package com.shayarishaala.shayarishaala

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.shayarishaala.shayarishaala.Model.Routing.ShayariRouting
import com.shayarishaala.shayarishaala.ui.theme.ShayariShaalaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShayariShaalaTheme {
                val navHostController = rememberNavController()
                ShayariRouting(navHostController = navHostController)
            }
        }
    }
}
