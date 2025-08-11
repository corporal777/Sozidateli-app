package com.examle.data.models.eventRegister

import com.examle.data.models.EventFormFieldModel

class EventRegisterFieldDate(
    field: EventFormFieldModel,
    var value: String?
) : EventRegistrationData(field) {

    override fun isValid() = !field.isRequired || !value.isNullOrEmpty()
    override fun hasForm() = !value.isNullOrEmpty()
}