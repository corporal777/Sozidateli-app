package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventSubscriptionResponse(
    val id : Int,
    val event : Int,
    val user : Int
) : Parcelable