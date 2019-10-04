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
        val status: Status?,
        val place: Place?,
        val organization: Organization?,
        val organization_id: String?,
        val event_code: String?

) : Parcelable {
    enum class Status {
        WAIT_VERIFY_USER,
        PENDING,
        APPROVED,
        DECLINED,
        CONFERENCE_ENDS
    }
}
