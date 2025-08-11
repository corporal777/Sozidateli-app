package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R
import com.example.ui.theme.CourseMoreTextColor
import com.example.ui.theme.MainBrownColor
import com.example.util.TextUtils

@Composable
fun TextWithIcon(rate: String, modifier: Modifier) {
    val modId = "modIcon"
    val text = buildAnnotatedString {
        appendInlineContent(modId, "[icon]")
        append(rate)

    }
    val inlineContent = mapOf(
        Pair(
            modId,
            InlineTextContent(Placeholder(18.sp, 18.sp, PlaceholderVerticalAlign.Center)) {
                Icon(
                    painterResource(R.drawable.ic_about_session),
                    "",
                    tint = MainBrownColor,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = (3).dp, top = 2.dp, end = 2.dp)
                )
            }
        )
    )

    Text(
        text = text,
        inlineContent = inlineContent,
        color = MainBrownColor,
        fontSize = 15.sp,
        fontFamily = TextUtils.font,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier,
        letterSpacing = TextUnit(-0.01F, TextUnitType.Sp),
        style = LocalTextStyle.current.copy(platformStyle = PlatformTextStyle(includeFontPadding = false))
    )
}