package com.examle.data.models.eventRegister

import com.example.data.models.EventFormFieldModel
import com.example.extensions.fromJson
import com.google.gson.JsonElement

class EventRegisterFieldCheckBox(
    field: EventFormFieldModel,
    var value: Set<String>?
) : EventRegistrationData(field) {

    override fun isValid() = !field.isRequired || !value.isNullOrEmpty()
    override fun hasForm() = !value.isNullOrEmpty()
}