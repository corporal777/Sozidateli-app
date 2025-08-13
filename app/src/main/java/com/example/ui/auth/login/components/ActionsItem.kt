package com.example.ui.auth.login.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.example.app.R
import com.example.ui.components.AppLoadingButton
import com.example.ui.components.AppPasswordTextField
import com.example.ui.components.AppTextField
import com.example.ui.components.TextNormal
import com.example.ui.components.TextRegular
import com.example.ui.theme.AuthHorizontalPadding
import com.example.ui.theme.DefaultHorizontalPadding
import com.example.ui.theme.InputTitleTextColor
import com.example.ui.theme.MainBrownColor
import com.example.ui.theme.VkBtnBackgroundColor

@Composable
fun ConstraintLayoutScope.ActionsItem(
    isEnabled : Boolean,
    isLoading : Boolean,
    password: ConstrainedLayoutReference,
    forget: ConstrainedLayoutReference,
    login: ConstrainedLayoutReference,
    onLogin : () -> Unit
) {

    TextNormal(
        text = stringResource(R.string.auth_forgot_password),
        fontSize = 14.sp,
        color = MainBrownColor,
        modifier = Modifier
            .constrainAs(forget) {
                top.linkTo(password.bottom, 25.dp)
                start.linkTo(parent.start, DefaultHorizontalPadding)
                end.linkTo(parent.end, DefaultHorizontalPadding)
            }
    )

    AppLoadingButton(
        text = AnnotatedString(stringResource(R.string.auth_label_login)),
        modifier = Modifier
            .padding(horizontal = DefaultHorizontalPadding)
            .constrainAs(login) {
                top.linkTo(forget.bottom, 35.dp)
            },
        isEnabled = isEnabled,
        isLoading = isLoading,
        onClick = onLogin
    )
}