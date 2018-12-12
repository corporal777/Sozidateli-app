package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(
        val id: String,
        val name: String,
        val logo: String,
        val info: String,
        val startDate: Long,
        val finishDate: Long,
        val organizationName: String,
        val status: Status,
        val buildingScheme: String
) : Parcelable

enum class Status(val code: Int) {
    APPROVED(1),
    CONFIRMATION_EXPECTED(2),
    FINISHED(3)
}