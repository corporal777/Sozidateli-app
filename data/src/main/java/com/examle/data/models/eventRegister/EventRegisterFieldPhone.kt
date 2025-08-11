package com.examle.data.models.eventRegister

import com.examle.data.models.EventFormFieldModel
import com.example.common.fromJson
import com.google.gson.JsonElement

class EventRegisterFieldPhone(
    field: EventFormFieldModel,
    result: JsonElement?
) : EventRegistrationData(field) {

    var value = result.fromJson<String>()

    override fun isValid(): Boolean {
        //return if (field.isRequired || !value.isNullOrEmpty()) Utils.isPhoneNumberValid(value)
        //else true
        return true
    }

    override fun hasForm() = !value.isNullOrEmpty()
}