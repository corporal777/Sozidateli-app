package com.example.data.models.eventRegister

import com.example.data.models.EventFormFieldModel
import com.example.extensions.fromJson
import com.google.gson.JsonElement

class EventRegisterFieldDate(
    field: EventFormFieldModel,
    var value: String?
) : EventRegistrationData(field) {

    override fun isValid() = !field.isRequired || !value.isNullOrEmpty()
    override fun hasForm() = !value.isNullOrEmpty()
}