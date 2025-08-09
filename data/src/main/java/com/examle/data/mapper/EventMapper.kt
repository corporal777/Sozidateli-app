package com.examle.data.mapper

import com.examle.data.models.EventListResponse
import com.examle.data.models.EventResponse
import com.examle.domain.model.EventModel
import com.examle.domain.model.EventStatus
import com.examle.domain.model.PaginationResponse
import com.example.common.calendar
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
        status = status,
        isStatusActionAvailable = when (status.value) {
            EventStatus.REGISTRATION.name,
            EventStatus.REGISTRATION_FINISHED.name,
            EventStatus.RUNNING.name,
            EventStatus.APPROVED.name -> true

            else -> false
        },
        state = state,
        userAgreement = userAgreement?.uri,
        image = image?.uri,
        address = address?.getShortAddress(),
        currentUserRegistration = binds?.currentUserRegistration,
        currentUserRegistrationState = binds?.currentUserRegistrationState,
        eventRegistrationState = binds?.eventRegistrationState
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