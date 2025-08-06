package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R
import com.example.extensions.clickable
import com.example.ui.theme.BtnBackgroundBrownColor


@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    text : String,
    textColor : Color = Color.White,
    textSize : Dp = dimensionResource(R.dimen.common_button_text_size),
    backgroundColor: Color = BtnBackgroundBrownColor,
    height: Dp = dimensionResource(R.dimen.common_button_min_height),
    onClick : () -> Unit
) {

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .height(height)
            .background(backgroundColor)
            .clickable(rippleColor = Color.White, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        TextSemibold(
            text = text,
            color = textColor,
            fontSize = textSize.value.sp,
        )
    }
}