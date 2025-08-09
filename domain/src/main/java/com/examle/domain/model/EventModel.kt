package com.examle.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class EventModel(
    val id: Int,
    val name: String,
    val holdingDate: String,
    val backgroundColor: String?,
    val status: EventsStatusModel? = null,
    val isStatusActionAvailable : Boolean,
    val state: EventStateModel? = null,
    val userAgreement: String? = null,
    val image: String? = null,
    val address: String? = null,
    val currentUserRegistration: CurrentUserRegistrationModel? = null,
    val currentUserRegistrationState: EventRegistrationStateModel? = null,
    val eventRegistrationState: EventRegistrationStateModel? = null,
) : Parcelable


enum class EventStatus {
    CANCELED,
    PREPARING,
    AWAITING,
    PENDING,
    APPROVED,
    DECLINED,
    BANNED,
    REGISTRATION,
    REGISTRATION_FINISHED,
    RUNNING,
    FINISHED,
    CONFERENCE_ENDS,
    IN_ARCHIVE
}
