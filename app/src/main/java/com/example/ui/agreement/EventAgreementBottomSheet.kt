package com.example.ui.agreement

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.examle.domain.model.event.EventModel
import com.example.app.R
import com.example.extensions.clickable
import com.example.ui.components.AppButton
import com.example.ui.components.AppCheckBox
import com.example.ui.components.AppLoadingButton
import com.example.ui.components.TextNormal
import com.example.ui.components.TextSemibold
import com.example.ui.theme.AmbientShadowColor
import com.example.ui.theme.AppBackgroundColor
import com.example.ui.theme.AuthHorizontalPadding
import com.example.ui.theme.BottomSheetBackgroundColor
import com.example.ui.theme.CheckBoxCheckedColor
import com.example.ui.theme.CheckBoxUncheckedColor
import com.example.ui.theme.SpotShadowColor
import com.google.firebase.messaging.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventAgreementBottomSheet(
    event: EventModel,
    viewModel: EventAgreementViewModel = hiltViewModel(),
    onDismiss: (EventModel?) -> Unit
) {

    val state = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var checked by remember { mutableStateOf(false) }

    val isLoadingState by viewModel.loading.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    LaunchedEffect(isLoadingState) { isLoading = isLoadingState }

    val eventState by viewModel.eventState.collectAsState()
    LaunchedEffect(eventState) {
        if (eventState != null) {
            scope.invokeHide(state) {
                onDismiss.invoke(eventState)
                viewModel.eventState.value = null
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss.invoke(null) },
        sheetState = state,
        containerColor = BottomSheetBackgroundColor,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = {}
    ) {
        ConstraintLayout(Modifier.fillMaxWidth()) {
            val (topIcon, close, title, policy, checkBox, action) = createRefs()

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .width(30.dp)
                    .height(4.dp)
                    .background(Color.DarkGray)
                    .constrainAs(topIcon) {
                        top.linkTo(parent.top, 5.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    })

            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "",
                tint = Color.Unspecified,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(48.dp)
                    .constrainAs(close) {
                        top.linkTo(parent.top, 5.dp)
                        end.linkTo(parent.end, 10.dp)
                    }
                    .clickable(Color.Black) {
                        scope.invokeHide(state) { onDismiss.invoke(null) }
                    }
                    .padding(2.dp)
            )

            TextSemibold(
                text = stringResource(R.string.event_register_agreement_open_error),
                color = Color.Black,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
                    .constrainAs(title) { top.linkTo(close.bottom, 20.dp) },
                textAlign = TextAlign.Center
            )

            TextNormal(
                text = stringResource(R.string.auth_agree_user_agreement),
                color = Color.Black,
                fontSize = 15.sp,
                modifier = Modifier
                    .constrainAs(policy) {
                        top.linkTo(title.bottom, 20.dp)
                        start.linkTo(checkBox.end, 10.dp)
                        end.linkTo(parent.end, 15.dp)
                        width = Dimension.fillToConstraints

                    }
            )

            AppCheckBox(
                modifier = Modifier
                    .constrainAs(checkBox) {
                        top.linkTo(policy.top)
                        bottom.linkTo(policy.bottom)
                        start.linkTo(parent.start, 15.dp)
                    },
                checked = checked,
                onCheckedChange = { checked = !checked }
            )

            AppLoadingButton(
                text = stringResource(R.string.auth_action_continue),
                isEnabled = checked,
                isLoading = isLoading,
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .constrainAs(action) {
                        top.linkTo(policy.bottom, 25.dp)
                        bottom.linkTo(parent.bottom, 10.dp)
                    }
            ) { viewModel.acceptEventAgreement(event) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun CoroutineScope.invokeHide(state: SheetState, onCompletion: (Throwable?) -> Unit) {
    launch { state.hide() }
        .invokeOnCompletion {
            if (!state.isVisible) {
                onCompletion.invoke(it)
            }
        }
}