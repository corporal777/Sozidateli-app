package com.examle.domain.model.event

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventStateModel(
    val isRunning: Boolean?,
    val isFinished: Boolean?,
    val isAvailable: Boolean?,
    val isFormEnabled: Boolean?,
    var agreementState: String?
) : Parcelable