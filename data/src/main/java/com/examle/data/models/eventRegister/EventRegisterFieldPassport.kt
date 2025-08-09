package com.examle.data.models.eventRegister

import com.example.data.models.EventFormFieldModel
import com.example.data.models.EventPassport
import com.example.extensions.fromJson
import com.google.gson.JsonElement

class EventRegisterFieldPassport(
    field: EventFormFieldModel,
    result: JsonElement?
) : EventRegistrationData(field) {

    var value = result.fromJson<EventPassport>()


    override fun isValid(): Boolean {
        return !field.isRequired || value != null && value!!.isDataComplete()
    }

    override fun hasForm(): Boolean {
        return value != null
    }
}