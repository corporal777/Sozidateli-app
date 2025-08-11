package com.examle.data.models.eventRegister

import com.examle.data.models.EventFormFieldModel

class EventRegisterFieldTitle(
    field: EventFormFieldModel,
    var value: String?
) : EventRegistrationData(field) {

    override fun isValid() = true
    override fun hasForm() = false

}