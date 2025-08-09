package com.examle.data.models.eventRegister

import com.example.data.models.EventFormFieldModel
import com.example.extensions.fromJson
import com.example.util.Utils
import com.google.gson.JsonElement

class EventRegisterFieldPhone(
    field: EventFormFieldModel,
    result: JsonElement?
) : EventRegistrationData(field) {

    var value = result.fromJson<String>()

    override fun isValid(): Boolean {
        return if (field.isRequired || !value.isNullOrEmpty()) Utils.isPhoneNumberValid(value)
        else true
    }

    override fun hasForm() = !value.isNullOrEmpty()
}