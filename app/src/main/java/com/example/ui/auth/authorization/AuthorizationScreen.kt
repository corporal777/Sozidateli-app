package com.example.ui.auth.authorization

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.auth.authorization.components.BackgroundItem
import com.example.ui.auth.authorization.components.ButtonsItem
import com.example.ui.auth.authorization.components.PagerItem
import com.example.ui.theme.AppBackgroundColor
import com.example.ui.theme.AuthHorizontalPadding

@Composable
fun AuthorizationScreen(
    padding: PaddingValues,
    viewModel: AuthorizationViewModel = hiltViewModel(),
    onLogin: () -> Unit
) {

    val storiesState by viewModel.stories.collectAsState()
    val context = LocalContext.current

    val isLoadingState by viewModel.loading.collectAsStateWithLifecycle()
    var isLoading by remember { mutableStateOf(false) }
    LaunchedEffect(isLoadingState) { isLoading = isLoadingState }

    val timerCount by viewModel.nextStory

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.startTimer() }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { viewModel.stopTimer() }


    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .background(AppBackgroundColor)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {

        val (logo, title, pager, indicator, reg, login, text, vk, gos) = createRefs()

        BackgroundItem(logo, padding)

        PagerItem(title, pager, indicator, storiesState, timerCount)

        ButtonsItem(isLoading, reg, login, text, vk, gos, onLogin) {
            viewModel.onAuthVkClick(context)
        }
    }

}

