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
        val userRegistration: Event.RegistrationStatus,
        @SerializedName("registration_name")
        val registrationName: String?,
        @SerializedName("registration_headline")
        val registrationHeadline: String?,
        @SerializedName("registration_subtitle")
        val registrationSubtitle: String?,
        @SerializedName("is_require_moderate_requests")
        val isRequireModerate: Boolean?
)
