package com.examle.domain.model.event

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventRegistrationModel(
    val availableActions: List<String>? = null,
    val registrationClosed: Boolean?,
    val profileLevel: Boolean? = null,
    val requiredLevel: String? = null
) : Parcelable