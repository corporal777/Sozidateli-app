package com.example.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R
import com.example.extensions.clickable
import com.example.ui.components.TextSemibold
import com.example.ui.theme.MainBrownColor

@Composable
fun AuthButtonItem(onClick : () -> Unit) {
    TextSemibold(
        modifier = Modifier
            .clip(CircleShape)
            .wrapContentWidth()
            .background(MainBrownColor)
            .clickable(Color.White, onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 3.dp),
        text = stringResource(R.string.auth_label_login),
        fontSize = 15.sp,
        color = Color.White,
        textAlign = TextAlign.Center
    )
}