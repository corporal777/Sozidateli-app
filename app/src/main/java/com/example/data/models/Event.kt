package com.example.data.models

import com.google.gson.annotations.SerializedName

data class Event(
        @SerializedName("event_id")
        val id: String,
        @SerializedName("event_code")
        val code: String,
        @SerializedName("organization_id")
        val organizationId: String?,
        val organization: Organization?,
        val address: String?,
        @SerializedName("short_address")
        val shortAddress: String?,
        @SerializedName("address_city")
        val addressCity: String?,
        val name: String,
        val logo: String?,
        @SerializedName("bg_color")
        val backgroundColor: String?,
        @SerializedName("bg_img")
        val backgroundImage: String?,
        @SerializedName("conference_start")
        val conferenceStart: String?,
        @SerializedName("conference_finish")
        val conferenceFinish: String?,
        @SerializedName("conference_first_activity_start")
        val conferenceFirstActivityStart: String?,
        @SerializedName("conference_last_activity_finish")
        val conferenceLastActivityFinish: String?,
        @SerializedName("registration_start")
        val registrationStart: String?,
        @SerializedName("registration_finish")
        val registrationFinish: String?,
        val status: Status?,
        @SerializedName("user_registration")
        val userRegistration: RegistrationStatus?,
        val format: EventFormat?,
        @SerializedName("format_custom")
        val formatCustom: String?,
        val activities: List<SubEvent>?,
        @SerializedName("is_favorite")
        var isInFavorites: Boolean,
        val email: List<EmailAffiliation>?,
        @SerializedName("conference_requests_receiving_date_end")
        val conferenceRegistrationFinishDate: String?,
        @SerializedName("conference_requests_receiving_closed")
        val conferenceRegistrationClosed: Boolean,
        @SerializedName("can_register")
        val canRegister: Boolean
) {

    enum class Status {
        CONFERENCE_ENDS,
        IN_ARCHIVE,
        REGISTRATION_PARTICIPANTS,
        REGISTRATION_PARTICIPANTS_ENDS,
        CONFERENCE_IN_PROGRESS
    }

    enum class RegistrationStatus {
        @SerializedName("pending")
        PENDING,

        @SerializedName("approved")
        APPROVED,

        @SerializedName("declined")
        DECLINED,

        @SerializedName("cancelled")
        CANCELLED
    }

    companion object {
        const val FILTER_CONTENT = "content"
        const val FILTER_ADDRESS = "address"
        const val FILTER_NAME = "name"
        const val FILTER_REGISTRATION = "is_registered"
        const val FILTER_DATE_START = "date_start"
        const val FILTER_DATE_FINISH = "date_end"
        const val FILTER_CATEGORY = "category"
        const val FILTER_FORMAT = "format"
        const val FILTER_SHOW_CANCELED = "show_canceled"

        const val FILTER_REGISTRATION_PENDING = "pending"
        const val FILTER_REGISTRATION_APPROVED = "approved"
        const val FILTER_REGISTRATION_DECLINED = "declined"
        const val FILTER_REGISTRATION_NOT_REGISTERED = "not_registered"
        const val FILTER_REGISTRATION_ANY_REGISTERED = "any"
    }
}

fun Event.takeFormat(): EventFormat? {
    return format ?: formatCustom?.let { customFormat ->
        EventFormat(name = customFormat)
    }
}
