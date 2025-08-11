package com.examle.data.models.event

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventRegistrationStateResponse(
    val availableActions: List<String>? = null,
    val prohibitions: ProhibitionsModel? = null
) : Parcelable

@Parcelize
data class ProhibitionsModel(
    val profileLevelToLow: ProfileLevelToLowModel? = null,
    val registrationClosed: Boolean
) : Parcelable

@Parcelize
data class ProfileLevelToLowModel(
    val value: Boolean,
    val requiredLevel: String? = null
) : Parcelable

@Parcelize
data class CurrentUserRegistrationResponse(
    val id: Int? = null,
    val event: Int? = null,
    val createdDate: String? = null,
    val user: Int? = null,
    val status: EventsStatusResponse? = null,
    val wasPresent: String? = null
) : Parcelable