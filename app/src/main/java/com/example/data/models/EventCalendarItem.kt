package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventCalendarItem(
        val id: Int,
        val user: Int? = null,
        val date: DateModel? = null,
        val entity: EventEntity? = null
): Parcelable

@Parcelize
data class EventEntity(
        val type: String? = null,
        val id: Int
): Parcelable