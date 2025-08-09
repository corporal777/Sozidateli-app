package com.example.ui.views.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AppBackgroundColor
import com.example.ui.theme.MainBrownColor

@Composable
fun ProgressDialog(show: Boolean) {
    if (show) {
        Dialog(onDismissRequest = { }) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .width(80.dp)
                    .height(80.dp)
                    .background(AppBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = MainBrownColor,
                    trackColor = Color.Transparent,
                    strokeWidth = (4).dp,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}