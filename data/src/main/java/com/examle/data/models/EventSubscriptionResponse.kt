package com.examle.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventSubscriptionResponse(
    val id : Int,
    val event : Int,
    val user : Int
) : Parcelable