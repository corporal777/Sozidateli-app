package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(
        val id: String,
        val name: String,
        val logo: String,
        val info: String,
        val start: String,
        val finish: String,
        val organizationName: String,
        val status: Status,
        val place: Place
) : Parcelable

enum class Status(val code: String) {
    APPROVED("APPROVED"),
    CONFIRMATION_EXPECTED("2"),
    FINISHED("3")
}