package com.example.ui.auth.authorization.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.example.app.R
import com.example.ui.components.TextBold
import com.example.ui.components.TextExtraBold
import com.example.ui.components.TextRegular
import com.example.ui.theme.AuthHorizontalPadding
import com.example.ui.theme.AuthPagerIndicatorSelectedColor
import com.example.ui.theme.AuthTitleShadowColor
import com.example.util.TextUtils

@Composable
fun ConstraintLayoutScope.PagerItem(
    title: ConstrainedLayoutReference,
    pager: ConstrainedLayoutReference,
    indicator: ConstrainedLayoutReference,
    stories: List<String>
) {

    TextExtraBold(
        text = stringResource(R.string.auth_pager_title_text),
        color = Color.White,
        fontSize = dimensionResource(R.dimen.auth_pager_title_text_size).value.sp,
        textAlign = TextAlign.Center,
        lineHeight = 30.sp,
        style = TextStyle(shadow = Shadow(AuthTitleShadowColor, Offset(0f, 4f), 6f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AuthHorizontalPadding)
            .constrainAs(title) {
                bottom.linkTo(pager.top, 20.dp)
            }
    )

    val pagerState = rememberPagerState(pageCount = { stories.size - 1 })
    HorizontalPager(
        modifier = Modifier
            .fillMaxWidth()
            .constrainAs(pager) {
                bottom.linkTo(indicator.top, 10.dp)
            },
        state = pagerState,
    ) { page ->
        TextRegular(
            text = stories.get(page),
            color = Color.White,
            fontSize = dimensionResource(R.dimen.auth_pager_story_text_size).value.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AuthHorizontalPadding)
        )
    }

    Row(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .constrainAs(indicator) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            },
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val color =
                if (pagerState.currentPage == iteration) AuthPagerIndicatorSelectedColor else Color.White
            Box(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(10.dp)
            )
        }
    }
}
