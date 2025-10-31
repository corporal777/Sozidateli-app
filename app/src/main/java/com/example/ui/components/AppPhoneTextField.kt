package com.example.ui.components

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R
import com.example.ui.theme.InputContainerFocusedColor
import com.example.ui.theme.InputContainerUnFocusedColor
import com.example.ui.theme.InputPlaceholderColor
import com.example.ui.theme.InputTextColor
import com.example.ui.theme.MainBrownColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPhoneTextField(
    modifier: Modifier,
    hint: String,
    text : String = "",
    onTextChange: (text: String) -> Unit
) {
    val mask: String = "+7"
    var inputText by remember { mutableStateOf(makeMaskedText(text, mask)) }
    val interactionSource = remember { MutableInteractionSource() }


    BasicTextField(
        value = makeMaskedText(inputText.text, mask),
        onValueChange = {
            inputText = it
            onTextChange.invoke(clearPhoneText(it.text))
        },
        modifier = modifier
            .defaultMinSize(
                minWidth = Dp.Unspecified,
                minHeight = dimensionResource(R.dimen.common_edit_text_min_height)
            ).fillMaxWidth(),
        interactionSource = interactionSource,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = dimensionResource(R.dimen.common_edit_text_size).value.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = TextUnit(-0.01F, TextUnitType.Sp)
        ),
        cursorBrush = SolidColor(MainBrownColor),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            value = text,
            visualTransformation = VisualTransformation.None,
            innerTextField = innerTextField,
            singleLine = true,
            enabled = true,
            interactionSource = interactionSource,
            contentPadding = PaddingValues(horizontal = 15.dp, vertical = 0.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = InputTextColor,
                unfocusedTextColor = InputTextColor,
                focusedContainerColor = InputContainerFocusedColor,
                unfocusedContainerColor = InputContainerUnFocusedColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = {
                if (inputText.text.isBlank()) {
                    Text(
                        modifier = Modifier.alpha(0.5f),
                        text = hint,
                        color = InputPlaceholderColor,
                        fontSize = dimensionResource(R.dimen.common_edit_text_size).value.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = TextUnit(-0.01F, TextUnitType.Sp)
                    )
                }
            }
        )
    }
}

private fun makeMaskedText(text: String, mask : String): TextFieldValue {
    var formattedText = mask + makeText(text)
    var textSelection = formattedText.length

    if (formattedText.length < mask.length || formattedText == mask){
        formattedText = mask
        textSelection = mask.length
    }
    else {
        formattedText = formatPhoneText(formattedText)
        textSelection = formattedText.length
    }

    return TextFieldValue(formattedText, TextRange(textSelection))
}

private fun makeText(text: String): String {
    if (text.isEmpty()) return ""
    else if (text.length < 2) return  ""
    else {
        val str = text.cleanStr().replace("+7", "").let {
            if (it.length > 10 && it.first() == '8') {
                it.replaceFirst("8", "")
            }
            else it
        }
        return if (str.length > 10) str.substring(0, 10) else str
    }
}

private fun String.cleanStr(): String {
    return replace(" ", "")
        .replace("(", "")
        .replace(")", "")
        .replace("-", "")
}

private fun formatPhoneText(text: String?): String {
    if (text.isNullOrBlank()) return ""
    else {
        return if (text.length <= 5)
            StringBuilder(text).insert(2, " ").toString()
        else if (text.length <= 8)
            StringBuilder(text).insert(2, " ").insert(6, " ").toString()
        else if (text.length <= 10) StringBuilder(text)
            .insert(2, " ")
            .insert(6, " ")
            .insert(10, " ").toString()
        else StringBuilder(text)
            .insert(2, " ")
            .insert(6, " ")
            .insert(10, " ")
            .insert(13, " ").toString()
    }
}

private fun clearPhoneText(text: String): String {
    if (text.isEmpty()) return ""
    else {
        val str = text.cleanStr()
        if (str.length > 12) return str.substring(0, 12)
        else return str
    }
}