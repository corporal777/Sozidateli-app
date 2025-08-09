package com.examle.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CurrentUserRegistrationModel(
    val id: Int? = null,
    val event: Int? = null,
    val createdDate: String? = null,
    val user: Int? = null,
    val status: EventsStatusModel? = null,
    val wasPresent: String? = null
) : Parcelable

