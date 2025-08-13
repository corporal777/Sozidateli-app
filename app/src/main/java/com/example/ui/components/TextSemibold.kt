package com.example.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.example.util.TextUtils

@Composable
fun TextSemibold(
    text: String?,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        text = text ?: "",
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontFamily = TextUtils.font,
        letterSpacing = letterSpacing ?: TextUnit(-0.01F, TextUnitType.Sp),
        textAlign = textAlign,
        lineHeight = lineHeight,
        fontWeight = FontWeight.SemiBold,
        overflow = overflow,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = null,
        style = style.copy(platformStyle = PlatformTextStyle(includeFontPadding = false))
    )
}

@Composable
fun TextAnnotatedSemibold(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    style: TextStyle = LocalTextStyle.current,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontFamily = TextUtils.font,
        letterSpacing = letterSpacing ?: TextUnit(-0.01F, TextUnitType.Sp),
        textAlign = textAlign,
        lineHeight = lineHeight,
        fontWeight = FontWeight.SemiBold,
        overflow = overflow,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = { },
        style = style.copy(platformStyle = PlatformTextStyle(includeFontPadding = false))
    )
}