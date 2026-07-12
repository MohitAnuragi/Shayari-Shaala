package com.shayarishaala.shayarishaala.commonUI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shayarishaala.shayarishaala.ui.theme.Purple40

/**
 * Shared top bar that matches the inline Row pattern used across
 * KalamScreen and ShayariAndQuotes (Purple40 background, white circular
 * back-button Card with black ArrowBack icon, ExtraBold Serif title in black).
 *
 * @param title      Title text shown in the bar.
 * @param onBackClick If non-null, the back button is shown and calls this lambda on tap.
 * @param actions    Optional trailing composable slot (icons, etc.) placed at the end of the Row.
 */
@Composable
fun AppTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Purple40)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (onBackClick != null) {
            // White circular card — exactly as used in KalamScreen
            Card(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onBackClick() },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(200.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
        }

        Text(
            text = title,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Serif,
            fontSize = 28.sp,
            color = Color.Black,
            modifier = Modifier
                .weight(1f)
        )

        // Trailing actions slot
        actions()
    }
}
