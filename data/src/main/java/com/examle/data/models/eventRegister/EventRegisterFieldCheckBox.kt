package com.examle.data.models.eventRegister

import com.examle.data.models.EventFormFieldModel

class EventRegisterFieldCheckBox(
    field: EventFormFieldModel,
    var value: Set<String>?
) : EventRegistrationData(field) {

    override fun isValid() = !field.isRequired || !value.isNullOrEmpty()
    override fun hasForm() = !value.isNullOrEmpty()
}