package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventFormat(
        val id: Int = ID_INVALID,
        val name: String
) : Parcelable {
    companion object {
        const val ID_INVALID = 0
    }
}