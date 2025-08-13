package com.example.ui.event.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.examle.domain.model.event.EventModel
import com.example.app.R
import com.example.extensions.bottomShadow
import com.example.extensions.clickable
import com.example.ui.components.TextSemibold
import com.example.ui.theme.BottomNavigationBarColor
import com.example.ui.theme.EventDetailIconsBackgroundColor
import com.example.ui.theme.MainBrownColor
import kotlin.math.abs

@Composable
fun TopBarItem(
    topPadding: Dp,
    scrollState: ScrollState,
    event : EventModel,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit
) {
    val scrollValue = scrollState.value
    val scrollOffset = abs(scrollValue / (1200).toFloat())

    val iconTint = if (scrollOffset <= 0.7) Color.White else MainBrownColor
    val addTint = if (scrollOffset <= 0.7) Color.Transparent else MainBrownColor
    val backTint = EventDetailIconsBackgroundColor.copy(0.5f - scrollOffset)

    val elevationOffset = abs(scrollValue / (400).toFloat())
    val scrollElevation = if (elevationOffset <= 0f) 0.dp
    else {
        if (elevationOffset < 5) elevationOffset.dp else 5.dp
    }
    val elevationState = animateDpAsState(scrollElevation)

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .bottomShadow(elevationState.value)
    ) {
        val (back, fav, box, share, add) = createRefs()


        Box(Modifier
            .fillMaxWidth()
            .background(BottomNavigationBarColor.copy(scrollOffset))
            .constrainAs(box){
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                height = Dimension.fillToConstraints
            }
        ) {}

        Icon(
            painter = painterResource(R.drawable.ic_back_white),
            contentDescription = "",
            tint = iconTint,
            modifier = Modifier
                .clip(CircleShape)
                .size(dimensionResource(R.dimen.toolbar_icons_size))
                .background(backTint)
                .clickable(Color.White) { onBackClick.invoke() }
                .padding(7.dp)
                .constrainAs(back) {
                    top.linkTo(parent.top, topPadding + 10.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                    start.linkTo(parent.start, 18.dp)
                }
                .let {
                    val x = if (scrollOffset <= 0.0f) 0.dp else {
                        val movedX = scrollOffset * 10
                        if (movedX <= 12) movedX.dp else 12.dp
                    }
                    it.offset(x = -(x))
                }
        )


        TextSemibold(
            text = stringResource(R.string.add_to_calendar),
            color = iconTint,
            fontSize = 11.5.sp,
            modifier = Modifier
                .clip(CircleShape)
                .background(backTint)
                .border(2.dp, addTint, CircleShape)
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .constrainAs(add){
                    top.linkTo(parent.top, topPadding + 10.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                    end.linkTo(fav.start, 10.dp)
                }
        )

        Icon(
            painter = painterResource(
                if (event.userFavorite != null) R.drawable.ic_star_filled
                else R.drawable.ic_star
            ),
            contentDescription = "",
            tint = iconTint,
            modifier = Modifier
                .clip(CircleShape)
                .size(dimensionResource(R.dimen.toolbar_icons_size))
                .background(backTint)
                .clickable(Color.White) { onAddClick.invoke() }
                .padding(6.dp)
                .constrainAs(fav) {
                    top.linkTo(parent.top, topPadding + 10.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                    end.linkTo(share.start, 10.dp)
                }
                .let {
                    val x = if (scrollOffset <= 0.0f) 0.dp else {
                        val movedX = scrollOffset * 5
                        if (movedX <= 6) movedX.dp else 6.dp
                    }
                    it.offset(x = (x))
                }
        )

        Icon(
            painter = painterResource(R.drawable.ic_share_white),
            contentDescription = "",
            tint = iconTint,
            modifier = Modifier
                .clip(CircleShape)
                .size(dimensionResource(R.dimen.toolbar_icons_size))
                .background(backTint)
                .clickable(Color.White) { onAddClick.invoke() }
                .padding(6.dp)
                .constrainAs(share) {
                    top.linkTo(parent.top, topPadding + 10.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                    end.linkTo(parent.end, 18.dp)
                }
                .let {
                    val x = if (scrollOffset <= 0.0f) 0.dp else {
                        val movedX = scrollOffset * 5
                        if (movedX <= 6) movedX.dp else 6.dp
                    }
                    it.offset(x = (x))
                }
        )
    }
}