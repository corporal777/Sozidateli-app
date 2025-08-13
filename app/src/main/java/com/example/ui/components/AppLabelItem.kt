package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun AppLabelItem(text : String, modifier: Modifier) {

    TextBold(
        modifier = modifier,
        text = text,
        color = Color.Black,
        fontSize = 22.sp
    )
}