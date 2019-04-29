package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(
        @SerializedName("event_id")
        val id: Int,
        val name: String?,
        val logo: String?,
        val info: String?,
        val conference_start: String?,
        val conference_finish: String?,
        val registration_start: String?,
        val registration_finish: String?,
        val status: String?,
        val place: Place?,
        val organization: Organization?,
        val organization_id: String?,
        val event_code: String?

) : Parcelable


enum class StatusEvent(val code: String) {
    APPROVED("APPROVED"),
    CONFIRMATION_EXPECTED("CONFIRMATION_EXPECTED"),
    CONFERENCE_ENDS("CONFERENCE_ENDS"),
    IN_ARCHIVE("IN_ARCHIVE"),
    IN_DRAFT("IN_DRAFT"),
    REGISTRATION_PARTICIPANTS("REGISTRATION_PARTICIPANTS"),
    REGISTRATION_PARTICIPANTS_ENDS("REGISTRATION_PARTICIPANTS_ENDS"),
    CONFERENCE_IN_PROGRESS("CONFERENCE_IN_PROGRESS")
}
