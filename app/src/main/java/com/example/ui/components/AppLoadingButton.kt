package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.app.R
import com.example.extensions.clickable
import com.example.ui.theme.BtnBackgroundBrownColor

@Composable
fun AppLoadingButton(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = Color.White,
    textSize: Dp = dimensionResource(R.dimen.common_button_text_size),
    backgroundColor: Color = BtnBackgroundBrownColor,
    height: Dp = dimensionResource(R.dimen.common_button_min_height),
    isLoading: Boolean = false,
    isEnabled: Boolean = true,
    icon: Int? = null,
    onClick: () -> Unit
) {


    ConstraintLayout(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .height(height)
            .alpha(if (isEnabled) 1f else 0.5f)
            .background(backgroundColor)
            .clickable(
                rippleColor = Color.Black,
                enabled = !isLoading && isEnabled,
                onClick = onClick
            )
    ) {
        val (title, load, image) = createRefs()

        TextSemibold(
            text = if (isLoading) "" else text,
            color = textColor,
            fontSize = textSize.value.sp,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(height - 15.dp)
                    .constrainAs(load) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                color = textColor,
                trackColor = Color.Transparent,
                strokeWidth = ((height - 15.dp) / 10),
                strokeCap = StrokeCap.Round
            )
        }

        if (icon != null) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(25.dp)
                    .constrainAs(image) {
                        start.linkTo(parent.start, 15.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    })
        }

    }

}