package com.example.ui.components

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Visibility
import androidx.core.view.isVisible
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.app.R
import com.example.data.models.Event
import com.example.data.models.EventNew
import com.example.extensions.calendar
import com.example.extensions.clickable
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.formatToDefaultDate
import com.example.extensions.isSameDay
import com.example.extensions.parseColor
import com.example.extensions.parseToDate
import com.example.extensions.verticalGradientBrush
import com.example.ui.theme.AmbientShadowColor
import com.example.ui.theme.AppBackgroundColor
import com.example.ui.theme.BtnBackgroundWhiteGhostColor
import com.example.ui.theme.DefaultHorizontalPadding
import com.example.ui.theme.SpotShadowColor
import com.example.ui.views.loading.CustomLoadingButton
import com.example.util.getColorStateList
import com.example.util.setImage

@Composable
fun EventCardItem(event: EventNew, isTemporary: Boolean) {

    var eventImage by remember { mutableStateOf("") }
    eventImage = event.image?.uri ?: ""

    var actionButtonText by remember { mutableStateOf(EventAction.NONE) }
    actionButtonText = getActionButtonText(event, isTemporary)

    var eventDate by remember { mutableStateOf("") }
    eventDate = getEventDate(event.holdingDate?.from, event.holdingDate?.to)

    var eventStatus by remember { mutableStateOf(Triple(false, 0,0)) }
    eventStatus = getEventStatus(event)

    ConstraintLayout(
        modifier = Modifier
            .background(Color.White)
            .clickable(Color.Black) { }
            .shadow(
                12.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = AmbientShadowColor,
                spotColor = SpotShadowColor
            )
    ) {

        val (image, gradient, title, date, location, status, action) = createRefs()

        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.event_item_height_new))
                .constrainAs(image) { top.linkTo(parent.top) },
            model = ImageRequest.Builder(LocalContext.current)
                .data(eventImage)
                .crossfade(300)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
                .background(verticalGradientBrush(listOf(Color.Black, Color.Transparent)))
                .constrainAs(gradient) { top.linkTo(image.top) }
        )

        TextBold(
            text = event.name ?: "Undefined",
            color = Color.White,
            fontSize = 21.sp,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .constrainAs(title) { top.linkTo(parent.top, 20.dp) }
        )

        TextSemibold(
            text = eventDate,
            color = Color.White,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .constrainAs(date) { top.linkTo(title.bottom, 15.dp) }
        )

        TextSemibold(
            text = event.address?.getShortAddress() ?: "Undefined",
            color = Color.White,
            fontSize = 15.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .constrainAs(location) { top.linkTo(date.bottom, 5.dp) }
        )

        if (eventStatus.first){
            TextSemibold(
                text = stringResource(eventStatus.third),
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .wrapContentWidth()
                    .height(30.dp)
                    .background(colorResource(eventStatus.second))
                    .padding(horizontal = 15.dp)
                    .constrainAs(status) {
                        top.linkTo(location.bottom, 20.dp)
                        start.linkTo(parent.start, 15.dp)
                    }
            )
        }


        if (actionButtonText != EventAction.NONE){
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .height(dimensionResource(R.dimen.event_detail_action_button_min_height))
                    .wrapContentWidth()
                    .background(BtnBackgroundWhiteGhostColor)
                    .clickable(rippleColor = Color.White) {}
                    .padding(horizontal = 24.dp)
                    .constrainAs(action) {
                        bottom.linkTo(parent.bottom, 20.dp)
                        end.linkTo(parent.end, 20.dp)
                    },
                contentAlignment = Alignment.Center
            ) {
                TextSemibold(
                    text = actionButtonText.text,
                    color = Color.Black,
                    fontSize = dimensionResource(R.dimen.common_button_text_size).value.sp,
                )
            }
        }

    }

}

private fun getActionButtonText(event: EventNew, isTemporary: Boolean): EventAction {
    val registrationState = event.binds?.currentUserRegistrationState
    val registrationClosed = registrationState?.prohibitions?.registrationClosed ?: false
    val actions = registrationState?.availableActions ?: arrayListOf("")

    return if (isTemporary) EventAction.AUTH
    else if (event.isStatusActionAvailable() && !registrationClosed) {
        if (actions.contains("register")) EventAction.REGISTER
        else if (actions.contains("withdraw")) EventAction.CANCEL
        else EventAction.NONE
    } else EventAction.NONE
}

private fun getEventStatus(event: EventNew): Triple<Boolean, Int, Int> {
    val status = event.status?.value
    val userRegistration = event.binds?.currentUserRegistration?.status?.value

    return if (status == Event.Status.FINISHED)
        Triple(true, R.color.event_status_finished_background, R.string.event_status_finished)
    else if (status == Event.Status.CANCELED)
        Triple(true, R.color.event_status_cancelled_background, R.string.event_status_cancelled)
    else if (userRegistration == Event.Status.APPROVED)
        Triple(true, R.color.event_status_approved_background, R.string.event_status_approved_new)
    else if (userRegistration == Event.Status.PENDING)
        Triple(
            true,
            R.color.event_status_wait_confirmation_background,
            R.string.event_status_wait_confirmation
        )
    else if (userRegistration == Event.Status.DECLINED)
        Triple(true, R.color.event_status_declined_background, R.string.event_status_decline_new)
    else if (userRegistration == Event.Status.REGISTRATION_FINISHED)
        Triple(
            true,
            R.color.event_status_wait_confirmation_background,
            R.string.event_closed_request
        )
    else Triple(false, R.color.event_status_finished_background, R.string.event_status_finished)
}

private fun getEventDate(from: String?, to: String?): String {
    val dateStart = from ?: return ""
    val dateEnd = to ?: return ""

    val startDate =
        dateStart.parseToDate(defaultServerDateFormatter)?.calendar() ?: return ""
    val finishDate =
        dateEnd.parseToDate(defaultServerDateFormatter)?.calendar() ?: return ""

    return if (startDate.isSameDay(finishDate)) dateStart.formatToDefaultDate() ?: ""
    else dateStart.formatToDefaultDate() + " - " + dateEnd.formatToDefaultDate()
}

enum class EventAction(val text: String) {
    AUTH("Участвовать"),
    REGISTER("Участвовать"),
    CANCEL("Отозвать заявку"),
    NONE("")
}