package com.examle.data.models.eventRegister

import com.examle.data.models.EventFormFieldModel
import com.example.common.fromJson
import com.google.gson.JsonElement

class EventRegisterFieldSelectBox(
    field: EventFormFieldModel,
    result: JsonElement?
) : EventRegistrationData(field) {

    var value = result.fromJson<String>()

    override fun isValid() = !field.isRequired || !value.isNullOrEmpty()
    override fun hasForm() = !value.isNullOrEmpty()
}