package com.example.ui.auth.login.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.example.app.R
import com.example.ui.components.AppTopBar
import com.example.ui.components.TextBold
import com.example.ui.theme.AuthHorizontalPadding

@Composable
fun ConstraintLayoutScope.HeaderItem(
    topBar: ConstrainedLayoutReference,
    logo: ConstrainedLayoutReference,
    title: ConstrainedLayoutReference,
) {

    AppTopBar(modifier = Modifier.constrainAs(topBar) {
        top.linkTo(parent.top)
    })

    Image(
        modifier = Modifier
            .size(dimensionResource(R.dimen.auth_login_circle_logo_height))
            .constrainAs(logo) {
                top.linkTo(topBar.bottom, 20.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
        painter = painterResource(R.drawable.ic_app_logo_circle),
        contentDescription = "logo",
        contentScale = ContentScale.FillBounds,
    )

    TextBold(
        text = stringResource(R.string.auth_title_login_fragment),
        color = Color.Black,
        fontSize = 22.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AuthHorizontalPadding)
            .constrainAs(title) {
                top.linkTo(logo.bottom, 20.dp)
            }
    )
}