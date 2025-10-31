package com.example.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Dimension
import com.example.app.R
import com.example.extensions.clickable
import com.example.ui.theme.CheckBoxCheckedColor
import com.example.ui.theme.CheckBoxUncheckedColor

@Composable
fun AppCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    text : String,
    textSize : TextUnit = 15.sp,
    textColor: Color = Color.Black,
    onCheckedChange: () -> Unit,
) {
    val icon = if (checked) R.drawable.ic_switch_enabled else R.drawable.ic_switch

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .size(28.dp)
                .padding(3.dp)
                .clickable { onCheckedChange.invoke() },
            painter = painterResource(icon),
            tint = Color.Unspecified,
            contentDescription = "",
        )

        TextNormal(
            text = text,
            color = textColor,
            fontSize = textSize,
            modifier = Modifier.padding(start = 10.dp)
        )

    }

}