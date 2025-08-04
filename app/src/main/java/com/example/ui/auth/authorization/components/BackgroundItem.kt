package com.example.ui.auth.authorization.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.example.app.R

@Composable
fun ConstraintLayoutScope.BackgroundItem(logo: ConstrainedLayoutReference, padding: PaddingValues) {
    Image(
        modifier = Modifier.fillMaxSize(),
        painter = painterResource(R.drawable.background_auth_very_small),
        contentDescription = "background",
        contentScale = ContentScale.FillBounds,
    )

    Image(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.auth_logo_text_height))
            .constrainAs(logo) {
                top.linkTo(parent.top, padding.calculateTopPadding() + 20.dp)
            },
        painter = painterResource(R.drawable.ic_app_logo_main),
        contentDescription = "logo",
    )
}