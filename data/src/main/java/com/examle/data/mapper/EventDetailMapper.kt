package com.examle.data.mapper

import com.examle.data.R
import com.examle.data.models.event.EventResponse
import com.examle.domain.model.event.EventActionStatus
import com.examle.domain.model.event.EventDetailModel
import com.examle.domain.model.event.EventRegistrationModel
import com.examle.domain.model.event.EventStateModel
import com.examle.domain.model.event.UserRegistrationModel
import com.example.common.calendar
import com.example.common.constants.EVENT_STATUS_APPROVED
import com.example.common.constants.EVENT_STATUS_CANCELED
import com.example.common.constants.EVENT_STATUS_REGISTRATION
import com.example.common.constants.EVENT_STATUS_REGISTRATION_FINISHED
import com.example.common.constants.EVENT_STATUS_RUNNING
import com.example.common.daysBetween
import com.example.common.defaultServerDateFormatter
import com.example.common.defaultServerDateTimeFormatter
import com.example.common.formatToDefaultDayMonthYearDate
import com.example.common.formatToDefaultTime
import com.example.common.isSameDay

internal fun EventResponse.mapToDomainEventDetailModel(isTemp: Boolean): EventDetailModel {
    val statusAction = when (status.value) {
        EVENT_STATUS_REGISTRATION,
        EVENT_STATUS_REGISTRATION_FINISHED,
        EVENT_STATUS_RUNNING,
        EVENT_STATUS_APPROVED -> true
        else -> false
    }

    return EventDetailModel(
        id = id,
        name = name,
        description = description ?: "",
        holdingDate = transformEventDate(this),
        requestDate = getEventRequestDate(this),
        backgroundColor = backgroundColor?.value,
        status = status.value,
        actionStatus = setActionStatus(this, isTemp, statusAction),
        isStatusActionAvailable = statusAction,
        isHasFormResult = isHasFormResult(),
        state = EventStateModel(
            isRunning = state?.isRunning,
            isFinished = state?.isFinished,
            isAvailable = state?.registration?.isAvailable,
            isFormEnabled = state?.registration?.formEnabled ?: false,
            agreementState = state?.agreement?.state
        ),
        userAgreement = userAgreement?.uri,
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

private fun transformEventDate(eventData: EventResponse): String {
    val dateStart = eventData.holdingDate?.from ?: return ""
    val dateEnd = eventData.holdingDate.to ?: return ""

    if (eventData.isHasOneActivity()) {
        val startDate = dateStart.calendar(defaultServerDateFormatter) ?: return ""
        val finishDate = dateEnd.calendar(defaultServerDateFormatter) ?: return ""

        if (startDate.isSameDay(finishDate)) {
            val firstDate = dateStart.formatToDefaultDayMonthYearDate() + " г."
            val firstTime = dateStart.formatToDefaultTime()
            val secondTime = dateEnd.formatToDefaultTime()
            return if (firstTime.isNullOrEmpty() || secondTime.isNullOrEmpty()) firstDate
            else "$firstDate, $firstTime - $secondTime"
        } else {
            val firstTime = dateStart.formatToDefaultTime() ?: ""
            val firstDate = dateStart.formatToDefaultDayMonthYearDate() + " г., " + firstTime
            val secondTime = dateEnd.formatToDefaultTime() ?: ""
            val secondDate = dateEnd.formatToDefaultDayMonthYearDate() + " г., " + secondTime
            return "$firstDate - $secondDate"
        }
    } else return dateStart.formatToDefaultDayMonthYearDate() + " г." +
            " - " + dateEnd.formatToDefaultDayMonthYearDate() + " г."
}

private fun setActionStatus(event: EventResponse, isTemp: Boolean, available: Boolean): EventActionStatus {
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

private fun getEventRequestDate(eventData: EventResponse): String? {
    val requestsApply = eventData.requestsApply ?: return null
    if (!requestsApply.dateFrom.isNullOrEmpty() && !requestsApply.dateLimit.isNullOrEmpty()) {
        val today = System.currentTimeMillis()
        val startReq = defaultServerDateTimeFormatter.parse(requestsApply.dateFrom)?.time ?: 0
        if (startReq > today) {
            val day = daysBetween(today, startReq)
            return when (day) {
                1 -> "До начала приема заявок $day день"
                in 2..4 -> "До начала приема заявок $day дня"
                else -> "До начала приема заявок $day дней"
            }
        } else {
            val limitDate = requestsApply.dateLimit.formatToDefaultDayMonthYearDate() + " г."
            val limitTime = requestsApply.dateLimit.formatToDefaultTime()
            return "Заявки принимаются по $limitDate, $limitTime"
        }
    } else return null
}