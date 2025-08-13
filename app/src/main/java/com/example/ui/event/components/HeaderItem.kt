package com.example.ui.event.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Visibility
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.examle.domain.model.event.EventActionStatus
import com.examle.domain.model.event.EventModel
import com.example.app.R
import com.example.extensions.verticalGradientBrush
import com.example.ui.components.AppLoadingButton
import com.example.ui.components.TextBold
import com.example.ui.components.TextMedium
import com.example.ui.components.TextNormal
import com.example.ui.components.TextSemibold
import com.example.ui.components.TextWithIcon
import com.example.ui.theme.ActionTextClosedColor
import com.example.ui.theme.ActionTextSmallColor
import com.example.ui.theme.BtnBackgroundApprovedColor
import com.example.ui.theme.BtnBackgroundBrownColor
import com.example.ui.theme.BtnBackgroundClosedColor
import com.example.ui.theme.BtnBackgroundGreenColor
import com.example.ui.theme.BtnBackgroundWhiteGhostColor
import com.example.ui.theme.CenterGradientColor
import com.example.ui.theme.DefaultHorizontalPadding
import com.example.ui.theme.EndGradientColor
import com.example.ui.theme.EventDetailDateColor
import com.example.ui.theme.MainBrownColor
import com.example.ui.theme.TopGradientColor

@Composable
fun HeaderItem(
    event: EventModel,
    isLoading : Boolean,
    onClick: (EventActionStatus) -> Unit
) {

    var showFormText by remember { mutableStateOf(AnnotatedString("")) }
    showFormText = getTextShowForm(event)

    var actionButtonText by remember { mutableStateOf(AnnotatedString("")) }
    actionButtonText = getActionButtonText(event.requestDate, event.actionStatus)

    var actionButtonColor by remember { mutableStateOf(BtnBackgroundGreenColor) }
    actionButtonColor = getActionButtonBackground(event.actionStatus)

    ConstraintLayout(Modifier.background(Color.Black)) {

        val (image, gradient, name, location, date, action, change, desc, more) = createRefs()

        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.event_image_height_new))
                .constrainAs(image) { top.linkTo(parent.top) },
            model = ImageRequest.Builder(LocalContext.current)
                .data(event.image)
                .crossfade(300)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .height(dimensionResource(R.dimen.event_image_shadow_height_new))
                .fillMaxWidth()
                .background(
                    verticalGradientBrush(
                        listOf(
                            TopGradientColor,
                            CenterGradientColor,
                            EndGradientColor
                        )
                    )
                )
                .constrainAs(gradient) { bottom.linkTo(image.bottom) }
        )

        TextBold(
            text = event.name,
            color = Color.White,
            fontSize = dimensionResource(if (event.name.length < 120) R.dimen.event_detail_name_text_size else R.dimen.event_detail_name_text_size_min).value.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DefaultHorizontalPadding)
                .constrainAs(name) { top.linkTo(gradient.top, 15.dp) }
        )

        TextSemibold(
            text = event.address ?: "",
            color = Color.White,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DefaultHorizontalPadding)
                .constrainAs(location) { top.linkTo(name.bottom, 10.dp) }
        )

        TextNormal(
            text = event.holdingDate,
            color = EventDetailDateColor,
            fontSize = 13.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DefaultHorizontalPadding)
                .constrainAs(date) { top.linkTo(location.bottom, 20.dp) }
        )

        AppLoadingButton(
            isLoading = isLoading,
            text = actionButtonText,
            textSize = dimensionResource(R.dimen.auth_buttons_text_size),
            height = dimensionResource(R.dimen.event_detail_action_button_min_height),
            backgroundColor = actionButtonColor,
            modifier = Modifier
                .padding(horizontal = DefaultHorizontalPadding)
                .constrainAs(action) {
                    top.linkTo(date.bottom, 20.dp)
                    visibility = if (event.actionStatus == EventActionStatus.NONE) Visibility.Gone
                    else Visibility.Visible
                }
        ) {  onClick.invoke(event.actionStatus) }

        TextMedium(
            textChar = showFormText,
            color = BtnBackgroundBrownColor,
            fontSize = 15.sp,
            modifier = Modifier
                .padding(horizontal = DefaultHorizontalPadding)
                .constrainAs(change) {
                    top.linkTo(action.bottom, 20.dp, 20.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    visibility = if (showFormText.text.isBlank()) Visibility.Gone
                    else Visibility.Visible
                }
        )

        TextNormal(
            text = event.description ?: "",
            color = Color.White,
            fontSize = 15.sp,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DefaultHorizontalPadding)
                .constrainAs(desc) { top.linkTo(change.bottom, 15.dp, 20.dp) }
        )

        TextWithIcon(
            rate = "Подробнее",
            modifier = Modifier
                .constrainAs(more) {
                    top.linkTo(desc.bottom, 15.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom, 10.dp)
                }
        )
    }
}

@Composable
private fun getActionButtonText(requestDate: String?, status: EventActionStatus): AnnotatedString {
    val stringRes: @Composable (Int, Color?, Int?) -> AnnotatedString = { res, color, size ->
        AnnotatedString(
            stringResource(res),
            SpanStyle(
                color ?: Color.Unspecified,
                if (size == null) TextUnit.Unspecified else dimensionResource(size).value.sp
            )
        )
    }

    val string: @Composable (String?, Color?, Int?) -> AnnotatedString = { res, color, size ->
        AnnotatedString(
            res ?: "",
            SpanStyle(
                color ?: Color.Unspecified,
                if (size == null) TextUnit.Unspecified else dimensionResource(size).value.sp
            )
        )
    }
    return when (status) {
        EventActionStatus.REGISTER, EventActionStatus.TEMPORARY -> {
            buildAnnotatedString {
                append(
                    stringRes(
                        R.string.event_action_participate,
                        null,
                        R.dimen.sub_event_description_text_size
                    )
                )
                append("\n")
                append(
                    string(
                        requestDate,
                        ActionTextSmallColor,
                        R.dimen.event_request_date_text_size
                    )
                )
            }
        }

        EventActionStatus.WITHDRAW ->
            buildAnnotatedString {
                append(stringRes(R.string.event_cancel_request, Color.Black, null))
            }

        EventActionStatus.CLOSED ->
            buildAnnotatedString {
                append(stringRes(R.string.event_closed_request, ActionTextClosedColor, null))
            }

        EventActionStatus.CANCELED ->
            buildAnnotatedString {
                append(stringRes(R.string.event_status_cancelled, ActionTextClosedColor, null))
            }

        EventActionStatus.UNSUBSCRIBE ->
            buildAnnotatedString {
                append(stringRes(R.string.event_unsubscribe_request, Color.Black, null))
            }

        EventActionStatus.VIEW ->
            buildAnnotatedString {
                append(stringRes(R.string.event_status_approved, Color.White, null))
            }

        EventActionStatus.SUBSCRIBE ->
            buildAnnotatedString {
                append(stringRes(R.string.event_subscribe_request, Color.White, null))
            }

        else -> AnnotatedString("")
    }
}

@Composable
private fun getTextShowForm(eventData: EventModel): AnnotatedString {
    val withDelimiter = when (eventData.actionStatus) {
        EventActionStatus.WITHDRAW -> false
        EventActionStatus.CANCELED -> false
        EventActionStatus.VIEW -> true
        else -> null
    }
    return if (withDelimiter == null) AnnotatedString("")
    else buildAnnotatedString {
        if (withDelimiter) append(stringResource(R.string.event_cancel_request))
        if (eventData.state?.isFormEnabled == true && eventData.isHasFormResult) {
            if (withDelimiter) append(" ∙ ")
            append(stringResource(R.string.my_event_form))
        }
    }
}

private fun getActionButtonBackground(status: EventActionStatus): Color {
    return when (status) {
        EventActionStatus.REGISTER, EventActionStatus.TEMPORARY, EventActionStatus.SUBSCRIBE -> BtnBackgroundGreenColor
        EventActionStatus.WITHDRAW -> BtnBackgroundWhiteGhostColor
        EventActionStatus.CLOSED -> BtnBackgroundClosedColor
        EventActionStatus.CANCELED -> BtnBackgroundClosedColor
        EventActionStatus.UNSUBSCRIBE -> BtnBackgroundWhiteGhostColor
        EventActionStatus.VIEW -> BtnBackgroundApprovedColor
        else -> BtnBackgroundGreenColor
    }
}
