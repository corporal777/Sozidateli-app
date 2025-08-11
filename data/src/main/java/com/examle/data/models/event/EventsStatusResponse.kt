package com.examle.data.models.event

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventsStatusResponse(
    val value: String,
    val changed: String? = null,
    val comments: String? = null
) : Parcelable