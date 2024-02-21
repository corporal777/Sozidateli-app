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
    var image: String?,
    var backgroundColor: String?,
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
    val userRegistration: Event.Status,
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
    val userAgreement: String?,
    val state : EventStateModel? = null,
    val formId : Int? = null,
    val form : EventFormModel? = null
) {
    companion object {
        const val MODERATION_MANUAL = "manual"
        const val MODERATION_AUTO_APPROVE = "auto_approve"
        const val MODERATION_AUTO_DISMISS = "auto_dismiss"

        fun setEventRegistration(event: EventNew): EventRegistration {
            return EventRegistration(
                id = event.id.toString(),
                organization = null,
                code = "",
                organizationId = event.binds?.organization?.id?.toString(),
                name = event.name ?: "",
                description = event.description ?: "",
                logo = null,
                image = event.image?.uri ?: event.binds?.organization?.image?.uri,
                backgroundColor = "",
                conferenceStart = event.holdingDate?.from,
                conferenceFinish = event.holdingDate?.to,
                registrationStart = null,
                registrationFinish = null,
                status = null,
                userAgreement = event.userAgreement?.uri,
                registrationName = null,
                userRegistration = event.status?.value ?: Event.Status.FINISHED,
                registrationHeadline = null,
                registrationSubtitle = null,
                isRequireModerate = null,
                moderateRegistration = null,
                conferenceFirstActivityStart = null,
                conferenceRegistrationFinishDate = event.requestsApply?.dateLimit,
                state = event.state,
                formId = event.binds?.form?.firstOrNull { e -> e.type == EventFormModel.Type.PARTICIPATION }?.id ?: 0,
                form = event.binds?.form?.firstOrNull { e -> e.type == EventFormModel.Type.PARTICIPATION }
            )
        }
    }


    fun setBackgroundColor(event: EventNew) {
        backgroundColor = when (form?.background) {
            EventFormModel.BackgroundType.EVENT -> event.backgroundColor?.value
            else -> event.binds?.organization?.backgroundColor?.value
        }
    }


}
