package com.examle.data.models.eventRegister

import com.examle.data.models.EventFormFieldModel

class EventRegisterFieldBoolean (
    field: EventFormFieldModel,
    var value: Boolean?
) : EventRegistrationData(field) {

    override fun isValid() : Boolean = if (field.isRequired) value == true else true
    override fun hasForm() = value != null
}