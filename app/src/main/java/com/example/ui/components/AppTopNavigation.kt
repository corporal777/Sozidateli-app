package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.app.R
import com.example.extensions.clickable
import com.example.ui.theme.AppBackgroundColor
import com.example.ui.theme.MainBrownColor

@Composable
fun AppTopNavigation(modifier: Modifier, title: String = "", offset: Float = 0f) {

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .background(AppBackgroundColor)
    ) {
        val (back, text) = createRefs()

        Icon(
            painter = painterResource(R.drawable.ic_back_black),
            tint = MainBrownColor,
            contentDescription = "",
            modifier = Modifier
                .clip(CircleShape)
                .size(40.dp)
                .constrainAs(back) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start, 4.dp)
                }
                .clickable(Color.Black) { }
                .padding(10.dp)
        )

        TextSemibold(
            text = title,
            color = Color.Black,
            fontSize = 17.sp,
            modifier = Modifier.constrainAs(text) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}