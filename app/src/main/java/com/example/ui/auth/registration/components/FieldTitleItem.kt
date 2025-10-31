package com.example.ui.auth.registration.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R
import com.example.ui.components.TextRegular
import com.example.ui.theme.DefaultHorizontalPadding
import com.example.ui.theme.InputTitleTextColor

@Composable
fun FieldTitleItem(text : String, top : Dp = 20.dp) {
    TextRegular(
        text = text,
        fontSize = dimensionResource(R.dimen.common_edit_text_title_size).value.sp,
        color = InputTitleTextColor,
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth().padding(top = top)
    )
}