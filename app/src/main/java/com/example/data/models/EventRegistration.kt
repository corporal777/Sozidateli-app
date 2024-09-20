package com.example.data.models

import com.google.gson.annotations.SerializedName

data class EventRegistration(
    val id: String,
    val name: String,
    val description: String?,
    var image: String?,
    var backgroundColor: String?,
    val conferenceStart: String?,
    val conferenceFinish: String?,
    val userRegistration: Event.Status,
    var registrationHeadline: String?,
    var registrationSubtitle: String?,
    val userAgreement: String?,
    val state: EventStateModel? = null,
    val formId: Int? = null,
    val form: EventFormModel? = null
) {
    companion object {
        const val MODERATION_MANUAL = "manual"
        const val MODERATION_AUTO_APPROVE = "auto_approve"
        const val MODERATION_AUTO_DISMISS = "auto_dismiss"

        fun setEventRegistration(event: EventNew): EventRegistration {
            return EventRegistration(
                id = (event.id ?: 0).toString(),
                name = event.name ?: "",
                description = event.description ?: "",
                image = event.image?.uri ?: event.binds?.organization?.image?.uri,
                backgroundColor = when (event.binds?.getParticipationForm()?.background) {
                    EventFormModel.BackgroundType.EVENT -> event.backgroundColor?.value
                    else -> event.binds?.organization?.backgroundColor?.value
                },
                conferenceStart = event.holdingDate?.from,
                conferenceFinish = event.holdingDate?.to,
                userAgreement = event.userAgreement?.uri,
                userRegistration = event.status?.value ?: Event.Status.FINISHED,
                registrationHeadline = event.binds?.getParticipationForm()?.title,
                registrationSubtitle = event.binds?.getParticipationForm()?.subtitle,
                state = event.state,
                formId = event.binds?.getParticipationForm()?.id ?: 0,
                form = event.binds?.getParticipationForm()
            )
        }
    }
}
