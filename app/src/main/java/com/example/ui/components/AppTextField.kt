package com.example.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
fun AppTextField(hint: String, modifier: Modifier, onTextChange: (text: String) -> Unit) {

    var text by rememberSaveable { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = text,
        onValueChange = {
            text = it
            onTextChange.invoke(it)
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
                Text(
                    modifier = Modifier.alpha(0.5f),
                    text = hint,
                    color = InputPlaceholderColor,
                    fontSize = dimensionResource(R.dimen.common_edit_text_size).value.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = TextUnit(-0.01F, TextUnitType.Sp)
                )
            }

        )
    }
}