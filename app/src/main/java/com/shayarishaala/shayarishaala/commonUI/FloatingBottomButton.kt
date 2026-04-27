package com.shayarishaala.shayarishaala.commonUI

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shayarishaala.shayarishaala.Model.Routing.ShayariRoutingItems

@Composable
fun FloatingBottomButton(navHostController: NavHostController) {
    Card(
        modifier = Modifier
            .size(56.dp) // Standard FAB size is usually 56dp
            .clickable {
                navHostController.navigate(ShayariRoutingItems.kalamScreen.route)
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD8B241)),
        shape = RoundedCornerShape(28.dp), // Half of size for circle
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Shayari",
                tint = Color.White
            )
        }
    }
}
