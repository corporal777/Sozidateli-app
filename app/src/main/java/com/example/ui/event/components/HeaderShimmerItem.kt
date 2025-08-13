package com.example.ui.event.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.app.R
import com.example.extensions.clickable
import com.example.extensions.shimmerEffect
import com.example.ui.components.TextSemibold
import com.example.ui.theme.AppBackgroundColor
import com.example.ui.theme.DefaultHorizontalPadding
import com.example.ui.theme.ShimmerBackgroundColor

@Composable
fun HeaderShimmerItem(padding: PaddingValues) {

    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .background(ShimmerBackgroundColor)
    ) {

        val (
            back,
            image,
            gradient,
            name1,
            name2,
            name3,
            name4,
            location,
            date,
            action,
            desc1,
            desc2,
            desc3,
            desc4,
            desc5,
            more) = createRefs()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.event_image_height_new))
                .constrainAs(image) { top.linkTo(parent.top) },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DefaultHorizontalPadding)
                .constrainAs(back) {
                    top.linkTo(parent.top, padding.calculateTopPadding() + 10.dp)
                },
            horizontalArrangement = Arrangement.SpaceBetween) {

            Box(
                Modifier
                    .clip(CircleShape)
                    .size(dimensionResource(R.dimen.toolbar_icons_size))
                    .shimmerEffect()
            )

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .height(dimensionResource(R.dimen.toolbar_icons_size))
                    .width(150.dp)
                    .shimmerEffect()
            )

            Spacer(Modifier.width(15.dp))

            Box(
                Modifier
                    .clip(CircleShape)
                    .size(dimensionResource(R.dimen.toolbar_icons_size))
                    .shimmerEffect()
            )

            Spacer(Modifier.width(15.dp))

            Box(
                Modifier
                    .clip(CircleShape)
                    .size(dimensionResource(R.dimen.toolbar_icons_size))
                    .shimmerEffect()
            )
        }

        Box(
            modifier = Modifier
                .height(dimensionResource(R.dimen.event_image_shadow_height_new))
                .fillMaxWidth()
                .constrainAs(gradient) { bottom.linkTo(image.bottom) }
        )

        Box(
            modifier = Modifier
                .height(15.dp)
                .constrainAs(name1) {
                    top.linkTo(gradient.top, 15.dp)
                    start.linkTo(parent.start, DefaultHorizontalPadding)
                    end.linkTo(parent.end, DefaultHorizontalPadding)
                    width = Dimension.fillToConstraints
                }
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .height(15.dp)
                .constrainAs(name2) {
                    top.linkTo(name1.bottom, 8.dp)
                    start.linkTo(parent.start, DefaultHorizontalPadding)
                    end.linkTo(parent.end, 70.dp)
                    width = Dimension.fillToConstraints
                }
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .height(15.dp)
                .constrainAs(name3) {
                    top.linkTo(name2.bottom, 8.dp)
                    start.linkTo(parent.start, DefaultHorizontalPadding)
                    end.linkTo(parent.end, 50.dp)
                    width = Dimension.fillToConstraints
                }
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .height(15.dp)
                .constrainAs(name4) {
                    top.linkTo(name3.bottom, 8.dp)
                    start.linkTo(parent.start, DefaultHorizontalPadding)
                    end.linkTo(parent.end, 50.dp)
                    width = Dimension.fillToConstraints
                }
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .width(300.dp)
                .height(12.dp)
                .constrainAs(location) {
                    top.linkTo(name4.bottom, 25.dp)
                    start.linkTo(parent.start, DefaultHorizontalPadding)
                }
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .width(200.dp)
                .height(10.dp)
                .constrainAs(date) {
                    top.linkTo(location.bottom, 25.dp)
                    start.linkTo(parent.start, DefaultHorizontalPadding)
                }
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .height(dimensionResource(R.dimen.event_detail_action_button_min_height))
                .constrainAs(action) {
                    top.linkTo(date.bottom, 25.dp)
                    start.linkTo(parent.start, DefaultHorizontalPadding)
                    end.linkTo(parent.end, DefaultHorizontalPadding)
                    width = Dimension.fillToConstraints
                }
                .shimmerEffect()
        ) { }

        Box(
            modifier = Modifier
                .height(12.dp)
                .constrainAs(desc1) {
                    top.linkTo(action.bottom, 25.dp)
                    start.linkTo(parent.start, DefaultHorizontalPadding)
                    end.linkTo(parent.end, DefaultHorizontalPadding)
                    width = Dimension.fillToConstraints
                }
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .height(12.dp)
                .constrainAs(desc2) {
                    top.linkTo(desc1.bottom, 8.dp)
                    start.linkTo(parent.start, DefaultHorizontalPadding)
                    end.linkTo(parent.end, DefaultHorizontalPadding)
                    width = Dimension.fillToConstraints
                }
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .height(12.dp)
                .constrainAs(desc3) {
                    top.linkTo(desc2.bottom, 8.dp)
                    start.linkTo(parent.start, DefaultHorizontalPadding)
                    end.linkTo(parent.end, DefaultHorizontalPadding)
                    width = Dimension.fillToConstraints
                }
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .height(12.dp)
                .constrainAs(desc4) {
                    top.linkTo(desc3.bottom, 8.dp)
                    start.linkTo(parent.start, DefaultHorizontalPadding)
                    end.linkTo(parent.end, DefaultHorizontalPadding)
                    width = Dimension.fillToConstraints
                }
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .height(12.dp)
                .constrainAs(desc5) {
                    top.linkTo(desc4.bottom, 8.dp)
                    start.linkTo(parent.start, DefaultHorizontalPadding)
                    end.linkTo(parent.end, DefaultHorizontalPadding)
                    width = Dimension.fillToConstraints
                }
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .width(150.dp)
                .height(15.dp)
                .constrainAs(more) {
                    top.linkTo(desc5.bottom, 25.dp)
                    start.linkTo(parent.start, DefaultHorizontalPadding)
                    end.linkTo(parent.end, DefaultHorizontalPadding)
                }
                .shimmerEffect()
        )

    }
}