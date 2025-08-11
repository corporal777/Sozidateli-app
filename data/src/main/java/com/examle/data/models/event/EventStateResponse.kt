package com.examle.data.models.event

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventStateModel(
    val isRunning: Boolean,
    val isFinished: Boolean,
    val isPublic: Boolean? = null,
    val isHidden: Boolean? = null,
    val rating: EventRatingModel? = null,
    val registration: EventRatingModel? = null,
    val agreement: EventAgreementState? = null
) : Parcelable


@Parcelize
data class EventRatingModel(
    val isAvailable: Boolean? = null,
    val formEnabled: Boolean? = false,
    val askDelay: Int? = null,
    val approvingMode: String? = null
) : Parcelable

@Parcelize
data class EventAgreementState(
    val state: String
) : Parcelable