package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notification(
        val id: Int,
        val title: String?,
        val message: String?,
        val date: String,
        val type: Type,
        var wasRead: Boolean,
        var acceptState: AcceptState = AcceptState.NONE,
        val rateId: Int? = null
) : Parcelable {

    enum class Type {
        SIMPLE, ACCEPTABLE, RATE
    }

    enum class AcceptState {
        NONE, ACCEPTED, CANCELED, DISABLED
    }
}