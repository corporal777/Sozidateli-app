package com.example.data.models.eventRegister

import com.example.data.models.EventFormFieldModel
import com.example.extensions.fromJson
import com.google.gson.JsonElement

class EventRegisterFieldBoolean (
    field: EventFormFieldModel,
    var value: Boolean?
) : EventRegistrationData(field) {

    override fun isValid() : Boolean = if (field.isRequired) value == true else true
    override fun hasForm() = value != null
}