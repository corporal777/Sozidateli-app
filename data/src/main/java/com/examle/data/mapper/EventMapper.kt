package com.examle.data.mapper

import android.util.Log
import com.examle.data.models.event.EventListResponse
import com.examle.data.models.event.EventResponse
import com.examle.domain.model.FavoriteModel
import com.examle.domain.model.PaginationResponse
import com.examle.domain.model.event.EventActionStatus
import com.examle.domain.model.event.EventModel
import com.examle.domain.model.event.EventRegistrationModel
import com.examle.domain.model.event.EventStateModel
import com.examle.domain.model.event.UserRegistrationModel
import com.examle.domain.model.organization.OrganizationModel
import com.examle.domain.model.user.SpeakerModel
import com.example.common.calendar
import com.example.common.constants.EVENT_STATUS_APPROVED
import com.example.common.constants.EVENT_STATUS_CANCELED
import com.example.common.constants.EVENT_STATUS_REGISTRATION
import com.example.common.constants.EVENT_STATUS_REGISTRATION_FINISHED
import com.example.common.constants.EVENT_STATUS_RUNNING
import com.example.common.daysBetween
import com.example.common.defaultServerDateFormatter
import com.example.common.defaultServerDateTimeFormatter
import com.example.common.formatToDefaultDate
import com.example.common.formatToDefaultDayMonthYearDate
import com.example.common.formatToDefaultTime
import com.example.common.isSameDay
import com.example.common.parseToDate


internal fun EventListResponse.mapToPagingDomainModel(isTemp: Boolean): PaginationResponse<EventModel> {
    return PaginationResponse(totalCount, data.map { it.mapToDomainEventModel(isTemp) })
}

internal fun EventResponse.mapToDomainEventModel(isTemp: Boolean): EventModel {
    val statusAction = when (status.value) {
        EVENT_STATUS_REGISTRATION,
        EVENT_STATUS_REGISTRATION_FINISHED,
        EVENT_STATUS_RUNNING,
        EVENT_STATUS_APPROVED -> true

        else -> false
    }

    return EventModel(
        id = id,
        name = name,
        holdingDate = transformEventDate(holdingDate?.from, holdingDate?.to),
        backgroundColor = backgroundColor?.value,
        status = status.value,
        actionStatus = setActionStatus(this, isTemp, statusAction),
        isStatusActionAvailable = statusAction,
        state = EventStateModel(
            isRunning = state?.isRunning,
            isFinished = state?.isFinished,
            isAvailable = state?.registration?.isAvailable,
            isFormEnabled = state?.registration?.formEnabled ?: false,
            agreementState = state?.agreement?.state,
            userAgreement = userAgreement?.uri
        ),
        image = image?.uri,
        address = address?.getShortAddress(),
        userRegistration = UserRegistrationModel(
            id = binds?.currentUserRegistration?.id,
            status = binds?.currentUserRegistration?.status?.value,
            wasPresent = binds?.currentUserRegistration?.wasPresent
        ),
        userRegistrationState = EventRegistrationModel(
            availableActions = binds?.currentUserRegistrationState?.availableActions,
            registrationClosed = binds?.currentUserRegistrationState?.prohibitions?.registrationClosed,
            profileLevel = binds?.currentUserRegistrationState?.prohibitions?.profileLevelToLow?.value,
            requiredLevel = binds?.currentUserRegistrationState?.prohibitions?.profileLevelToLow?.requiredLevel
        ),
        eventRegistrationState = EventRegistrationModel(
            availableActions = binds?.currentUserRegistrationState?.availableActions,
            registrationClosed = binds?.currentUserRegistrationState?.prohibitions?.registrationClosed,
            profileLevel = binds?.currentUserRegistrationState?.prohibitions?.profileLevelToLow?.value,
            requiredLevel = binds?.currentUserRegistrationState?.prohibitions?.profileLevelToLow?.requiredLevel
        )
    )
}

private fun transformEventDate(from: String?, to: String?): String {
    val dateStart = from ?: return ""
    val dateEnd = to ?: return ""

    val startDate =
        dateStart.parseToDate(defaultServerDateFormatter)?.calendar() ?: return ""
    val finishDate =
        dateEnd.parseToDate(defaultServerDateFormatter)?.calendar() ?: return ""

    return if (startDate.isSameDay(finishDate)) dateStart.formatToDefaultDate() ?: ""
    else dateStart.formatToDefaultDate() + " - " + dateEnd.formatToDefaultDate()
}


private fun setActionStatus(
    event: EventResponse,
    isTemp: Boolean,
    available: Boolean
): EventActionStatus {
    val state = event.binds?.currentUserRegistrationState
    val actions = state?.availableActions ?: arrayListOf("")
    val registrationClosed = state?.prohibitions?.registrationClosed

    return if (isTemp) EventActionStatus.TEMPORARY
    else if (available && state != null) {
        if (registrationClosed == true) EventActionStatus.CLOSED
        else if (actions.contains("register")) EventActionStatus.REGISTER
        else if (actions.contains("withdraw")) EventActionStatus.WITHDRAW
        else if (actions.contains("view")) EventActionStatus.VIEW
        else EventActionStatus.NONE
    } else if (event.status.value == EVENT_STATUS_CANCELED) EventActionStatus.CANCELED
    else {
        if (actions.contains("subscribe")) {
            if (event.binds?.isUserSubscribed == true) EventActionStatus.UNSUBSCRIBE
            else EventActionStatus.SUBSCRIBE
        } else EventActionStatus.NONE
    }
}