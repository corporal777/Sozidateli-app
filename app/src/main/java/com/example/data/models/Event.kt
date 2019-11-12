package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(
        @SerializedName("event_id")
        val id: String,
        @SerializedName("event_code")
        val code: String,
        @SerializedName("organization_id")
        val organizationId: String?,
        val organization: Organization?,
        val address: String?,
        val name: String,
        val logo: String?,
        @SerializedName("bg_color")
        val backgroundColor: String?,
        @SerializedName("conference_start")
        val conferenceStart: String?,
        @SerializedName("conference_finish")
        val conferenceFinish: String?,
        @SerializedName("registration_start")
        val registrationStart: String?,
        @SerializedName("registration_finish")
        val registrationFinish: String?,
        val status: Status?,
        @SerializedName("user_registration")
        val userRegistration: RegistrationStatus?,
        val format: EventFormat?

) : Parcelable {

    fun isCanRegister() = isCanRegister(status, userRegistration)

    enum class Status {
        CONFERENCE_ENDS,
        REGISTRATION_PARTICIPANTS
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

        const val FILTER_REGISTRATION_PENDING = "pending"
        const val FILTER_REGISTRATION_APPROVED = "approved"
        const val FILTER_REGISTRATION_DECLINED = "declined"
        const val FILTER_REGISTRATION_NOT_REGISTERED = "not_registered"
        const val FILTER_REGISTRATION_ANY_REGISTERED = "any"

        fun isCanRegister(eventStatus: Status?, registrationStatus: RegistrationStatus?): Boolean {
            return eventStatus == Status.REGISTRATION_PARTICIPANTS
                    && registrationStatus != RegistrationStatus.APPROVED
                    && registrationStatus != RegistrationStatus.PENDING
        }
    }
}
