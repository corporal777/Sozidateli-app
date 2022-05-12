package com.example.data.models

import com.google.gson.annotations.SerializedName

data class EventRegistration(
        @SerializedName("event_id")
        val id: String,
        @SerializedName("event_code")
        val code: String,
        @SerializedName("organization_id")
        val organizationId: String?,
        val organization: Organization?,
        val name: String,
        val description: String?,
        val logo: String?,
        @SerializedName("conference_start")
        val conferenceStart: String?,
        @SerializedName("conference_finish")
        val conferenceFinish: String?,
        @SerializedName("registration_start")
        val registrationStart: String?,
        @SerializedName("registration_finish")
        val registrationFinish: String?,
        val status: Event.RegistrationStatus?,
        @SerializedName("user_registration")
        val userRegistration: Event./*RegistrationStatus*/Status,
        @SerializedName("registration_name")
        val registrationName: String?,
        @SerializedName("registration_headline")
        var registrationHeadline: String?,
        @SerializedName("registration_subtitle")
        var registrationSubtitle: String?,
        @SerializedName("is_require_moderate_requests")
        val isRequireModerate: Boolean?,
        @SerializedName("moderate_registration")
        val moderateRegistration: String?,
        @SerializedName("conference_first_activity_start")
        val conferenceFirstActivityStart: String?,
        @SerializedName("conference_requests_receiving_date_end")
        val conferenceRegistrationFinishDate: String?,
        @SerializedName("user_agreement")
        val userAgreement: String?
) {
    companion object {
        const val MODERATION_MANUAL = "manual"
        const val MODERATION_AUTO_APPROVE = "auto_approve"
        const val MODERATION_AUTO_DISMISS = "auto_dismiss"
    }
}
