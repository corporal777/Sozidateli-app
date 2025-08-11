package com.examle.data.models.eventRegister

import com.examle.data.models.EventFormFieldModel
import com.google.gson.JsonElement

class EventRegisterFieldFile(
    field: EventFormFieldModel,
    result: JsonElement?
) : EventRegistrationData(field) {

    //var value = result.fromJson(EventFile.Deserializer())

    override fun isValid(): Boolean {
        //val file = value
        return !field.isRequired
        //return !field.isRequired || file != null && file.name.isNotEmpty()
    }

    override fun hasForm(): Boolean {
        //val file = value
        return true
        //return file != null && file.name.isNotEmpty()
    }
}