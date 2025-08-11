package com.examle.data.mapper

import android.util.Log
import com.examle.data.models.event.EventListResponse
import com.examle.data.models.event.EventResponse
import com.examle.domain.model.PaginationResponse
import com.examle.domain.model.event.EventModel
import com.examle.domain.model.event.EventRegistrationModel
import com.examle.domain.model.event.EventStateModel
import com.examle.domain.model.event.UserRegistrationModel
import com.example.common.calendar
import com.example.common.constants.EVENT_STATUS_APPROVED
import com.example.common.constants.EVENT_STATUS_REGISTRATION
import com.example.common.constants.EVENT_STATUS_REGISTRATION_FINISHED
import com.example.common.constants.EVENT_STATUS_RUNNING
import com.example.common.defaultServerDateFormatter
import com.example.common.formatToDefaultDate
import com.example.common.isSameDay
import com.example.common.parseToDate


internal fun EventListResponse.mapToPagingDomainModel(): PaginationResponse<EventModel> {
    return PaginationResponse(totalCount, data.map { it.mapToDomainModel() })
}

internal fun EventResponse.mapToDomainModel(): EventModel {
    return EventModel(
        id = id,
        name = name,
        holdingDate = transformEventDate(holdingDate?.from, holdingDate?.to),
        backgroundColor = backgroundColor?.value,
        status = status.value,
        isStatusActionAvailable = when (status.value) {
            EVENT_STATUS_REGISTRATION,
            EVENT_STATUS_REGISTRATION_FINISHED,
            EVENT_STATUS_RUNNING,
            EVENT_STATUS_APPROVED -> true
            else -> false
        },
        state = EventStateModel(
            isRunning = state?.isRunning,
            isFinished = state?.isFinished,
            isAvailable = state?.registration?.isAvailable,
            isFormEnabled = state?.registration?.formEnabled,
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