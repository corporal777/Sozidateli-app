package com.example.data.models

import com.google.gson.annotations.SerializedName

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
        val place: List<Place>?,
        val organization: Organization?,
        val organization_id: String?,
        val event_code: String?

) {
    enum class Status {
        @SerializedName("pending")
        PENDING,
        @SerializedName("approved")
        APPROVED,
        @SerializedName("declined")
        DECLINED
    }
}
