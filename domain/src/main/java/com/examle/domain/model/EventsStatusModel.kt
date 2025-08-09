package com.examle.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventsStatusModel(
    val value: String,
    val changed: String? = null,
    val comments: String? = null
) : Parcelable