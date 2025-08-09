package com.examle.data.models

data class EventRegistration(
    val id: String,
    var registrationHeadline: String?,
    var registrationSubtitle: String?,
    val conferenceStart: String?,
    val conferenceFinish: String?,
    val form: EventFormModel? = null,
    val formFields: List<EventFormFieldModel>,
    val formResult: List<EventFormResultFieldModel>
) {
    companion object {
        const val MODERATION_MANUAL = "manual"
        const val MODERATION_AUTO_APPROVE = "auto_approve"
        const val MODERATION_AUTO_DISMISS = "auto_dismiss"

        fun createData(
            event: EventResponse,
            fields: List<EventFormFieldModel>,
            result: List<EventFormResultFieldModel>
        ): EventRegistration {
            return EventRegistration(
                id = (event.id ?: 0).toString(),
                registrationHeadline = event.binds?.getForm()?.title,
                registrationSubtitle = event.binds?.getForm()?.subtitle,
                conferenceStart = event.holdingDate?.from,
                conferenceFinish = event.holdingDate?.to,
                form = event.binds?.getForm(),
                fields,
                result
            )
        }
    }
}
