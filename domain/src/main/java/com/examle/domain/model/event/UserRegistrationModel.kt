package com.examle.domain.model.event

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserRegistrationModel(
    val id: Int? = null,
    val status: String? = null,
    val wasPresent: String? = null
) : Parcelable