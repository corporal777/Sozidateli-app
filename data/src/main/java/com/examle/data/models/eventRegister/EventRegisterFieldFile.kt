package com.examle.data.models.eventRegister

import com.example.data.models.EventFile
import com.example.data.models.EventFormFieldModel
import com.example.extensions.fromJson
import com.google.gson.JsonElement

class EventRegisterFieldFile(
    field: EventFormFieldModel,
    result: JsonElement?
) : EventRegistrationData(field) {

    var value = result.fromJson(EventFile.Deserializer())

    override fun isValid(): Boolean {
        val file = value
        return !field.isRequired || file != null && file.name.isNotEmpty()
    }

    override fun hasForm(): Boolean {
        val file = value
        return file != null && file.name.isNotEmpty()
    }
}