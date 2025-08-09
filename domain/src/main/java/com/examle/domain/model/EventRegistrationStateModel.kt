package com.examle.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventRegistrationStateModel(
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