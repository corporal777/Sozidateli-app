package com.example.ui.auth.login.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.example.app.R
import com.example.ui.components.AppPasswordTextField
import com.example.ui.components.AppTextField
import com.example.ui.components.TextRegular
import com.example.ui.theme.DefaultHorizontalPadding
import com.example.ui.theme.InputTitleTextColor

@Composable
fun ConstraintLayoutScope.ContentItem(
    head: ConstrainedLayoutReference,
    loginTitle: ConstrainedLayoutReference,
    login: ConstrainedLayoutReference,
    passwordTitle: ConstrainedLayoutReference,
    password: ConstrainedLayoutReference,
    onLoginChange : (String) -> Unit,
    onPasswordChange : (String) -> Unit
) {

    TextRegular(
        text = stringResource(R.string.login),
        fontSize = dimensionResource(R.dimen.common_edit_text_title_size).value.sp,
        color = InputTitleTextColor,
        modifier = Modifier.constrainAs(loginTitle) {
            top.linkTo(head.bottom, 20.dp)
            start.linkTo(parent.start, DefaultHorizontalPadding)
        }
    )

    AppTextField(
        hint = stringResource(R.string.auth_input_login),
        modifier = Modifier
            .constrainAs(login) { top.linkTo(loginTitle.bottom, 5.dp) }
            .padding(horizontal = DefaultHorizontalPadding),
        onTextChange = onLoginChange
    )

    TextRegular(
        text = stringResource(R.string.password_confirm_hint),
        fontSize = dimensionResource(R.dimen.common_edit_text_title_size).value.sp,
        color = InputTitleTextColor,
        modifier = Modifier.constrainAs(passwordTitle) {
            top.linkTo(login.bottom, 20.dp)
            start.linkTo(parent.start, DefaultHorizontalPadding)
        }
    )

    AppPasswordTextField(
        hint = "********",
        modifier = Modifier
            .constrainAs(password) { top.linkTo(passwordTitle.bottom, 5.dp) }
            .padding(horizontal = DefaultHorizontalPadding),
        onTextChange = onPasswordChange
    )
}