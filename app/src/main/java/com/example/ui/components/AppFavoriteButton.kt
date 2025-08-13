package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.app.R
import com.example.extensions.clickable
import com.example.ui.theme.BtnBackgroundBrownColor
import com.example.ui.theme.BtnFavoriteBackgroundColor
import com.example.ui.theme.CheckBoxCheckedColor

@Composable
fun AppFavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onClick: () -> Unit
) {

    Row(
        modifier = modifier
            .clickable(
                rippleColor = Color.Black,
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star),
            contentDescription = "",
            tint = Color.Unspecified,
            modifier = Modifier
                .clip(CircleShape)
                .size(30.dp)
                .background(BtnFavoriteBackgroundColor)
                .padding(5.dp)
        )

        TextSemibold(
            text = stringResource(
                if (isFavorite) R.string.remove_from_favorites
                else R.string.add_to_favorites
            ),
            color = CheckBoxCheckedColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 10.dp)
        )
    }

}