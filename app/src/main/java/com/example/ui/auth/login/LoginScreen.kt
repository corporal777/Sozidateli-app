package com.example.ui.auth.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.app.R
import com.example.navigation.Route
import com.example.ui.auth.authorization.components.BackgroundItem
import com.example.ui.auth.authorization.components.ButtonsItem
import com.example.ui.auth.authorization.components.PagerItem
import com.example.ui.auth.login.components.ActionsItem
import com.example.ui.auth.login.components.ContentItem
import com.example.ui.auth.login.components.HeaderItem
import com.example.ui.components.AppPasswordTextField
import com.example.ui.components.AppTextField
import com.example.ui.components.AppTopBar
import com.example.ui.components.TextBold
import com.example.ui.components.TextRegular
import com.example.ui.theme.AppBackgroundColor
import com.example.ui.theme.AuthHorizontalPadding
import com.example.ui.theme.AuthTitleShadowColor
import com.example.ui.theme.DefaultHorizontalPadding
import com.example.ui.theme.InputTitleTextColor

@Composable
fun LoginScreen(
    paddingValues: PaddingValues,
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit
) {

    val isLoadingState by viewModel.loading.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    val isEnabledState by viewModel.isEnabled.collectAsState()
    var isEnabled by remember { mutableStateOf(false) }

    val isLoginSuccess by viewModel.isLoginSuccess.collectAsState()

    LaunchedEffect(isLoadingState) { isLoading = isLoadingState }
    LaunchedEffect(isEnabledState) { isEnabled = isEnabledState }
    LaunchedEffect(isLoginSuccess) {
        if (isLoginSuccess) onNavigate(Route.HomeScreen.route)
    }

    val focusManager = LocalFocusManager.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundColor)
            .padding(top = paddingValues.calculateTopPadding(),)
    ) {
        val (topBar, logo, title, loginTitle, loginInput, passwordTitle, password, forget, action) = createRefs()

        HeaderItem(topBar, logo, title)
        ContentItem(
            title,
            loginTitle,
            loginInput,
            passwordTitle,
            password,
            onLoginChange = { viewModel.onChangeLogin(it) },
            onPasswordChange = { viewModel.onChangePassword(it) }
        )
        ActionsItem(isEnabled, isLoading, password, forget, action) {
            focusManager.clearFocus()
            viewModel.onLoginClick(-1)
        }
    }
}




