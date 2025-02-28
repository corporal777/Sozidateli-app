package com.example.data.models.eventRegister

import com.example.data.models.EventFormFieldModel
import com.example.extensions.fromJson
import com.google.gson.JsonElement

class EventRegisterFieldTitle(
    field: EventFormFieldModel,
    var value: String?
) : EventRegistrationData(field) {

    override fun isValid() = true
    override fun hasForm() = false

}