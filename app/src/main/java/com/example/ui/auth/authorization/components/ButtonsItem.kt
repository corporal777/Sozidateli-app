package com.example.ui.auth.authorization.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.example.app.R
import com.example.extensions.clickable
import com.example.ui.components.AppButton
import com.example.ui.components.AppCornersButton
import com.example.ui.components.AppLoadingButton
import com.example.ui.components.TextNormal
import com.example.ui.theme.AuthHorizontalPadding
import com.example.ui.theme.GosUslugiBtnTextColor
import com.example.ui.theme.VkBtnBackgroundColor

@Composable
fun ConstraintLayoutScope.ButtonsItem(
    vkLoading : Boolean,
    reg: ConstrainedLayoutReference,
    login: ConstrainedLayoutReference,
    text: ConstrainedLayoutReference,
    vk: ConstrainedLayoutReference,
    gos: ConstrainedLayoutReference,
    onLogin: () -> Unit,
    onRegister : () -> Unit,
    onVk: () -> Unit
) {

    AppButton(
        text = stringResource(R.string.auth_action_register),
        textSize = dimensionResource(R.dimen.auth_buttons_text_size),
        height = dimensionResource(R.dimen.auth_buttons_min_height),
        modifier = Modifier
            .padding(horizontal = AuthHorizontalPadding)
            .constrainAs(reg) {
                bottom.linkTo(login.top, 15.dp)
            },
        onClick = onRegister
    )

    AppCornersButton(
        text = stringResource(R.string.auth_label_login),
        textSize = dimensionResource(R.dimen.auth_buttons_text_size),
        height = dimensionResource(R.dimen.auth_buttons_min_height),
        modifier = Modifier
            .padding(horizontal = AuthHorizontalPadding)
            .constrainAs(login) { bottom.linkTo(text.top, 20.dp) },
        onClick = onLogin
    )

    TextNormal(
        text = stringResource(R.string.or_auth_in_one_click),
        color = Color.White,
        fontSize = dimensionResource(R.dimen.auth_buttons_title_text_size).value.sp,
        textAlign = TextAlign.Center,
        lineHeight = 20.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AuthHorizontalPadding)
            .constrainAs(text) {
                bottom.linkTo(vk.top, 10.dp)
            }
    )

    AppLoadingButton(
        isLoading = vkLoading,
        text = stringResource(R.string.auth_label_login_with_vk),
        textSize = dimensionResource(R.dimen.auth_buttons_text_size),
        height = dimensionResource(R.dimen.auth_buttons_min_height),
        backgroundColor = VkBtnBackgroundColor,
        icon = R.drawable.ic_vk_logo,
        modifier = Modifier
            .padding(horizontal = AuthHorizontalPadding)
            .constrainAs(vk) {
                bottom.linkTo(gos.top, 15.dp)
            },
        onClick = onVk
    )

    AppLoadingButton(
        text = stringResource(R.string.auth_label_login_with_gos),
        textSize = dimensionResource(R.dimen.auth_buttons_text_size),
        textColor = GosUslugiBtnTextColor,
        height = dimensionResource(R.dimen.auth_buttons_min_height),
        backgroundColor = Color.White,
        icon = R.drawable.ic_gos_uslugi_logo,
        modifier = Modifier
            .padding(horizontal = AuthHorizontalPadding)
            .constrainAs(gos) {
                bottom.linkTo(parent.bottom, 20.dp)
            }
    ) { }
}