package com.example.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.app.R
import com.example.extensions.clickable
import com.example.ui.theme.CheckBoxCheckedColor
import com.example.ui.theme.CheckBoxUncheckedColor

@Composable
fun AppCheckBox(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon = if (checked) R.drawable.ic_switch_enabled else R.drawable.ic_switch

    Icon(
        modifier = modifier
            .clip(CircleShape)
            .size(28.dp)
            .padding(3.dp)
            .clickable { onCheckedChange.invoke() },
        painter = painterResource(icon),
        tint = Color.Unspecified,
        contentDescription = "",
    )
}