package com.examle.data.models.eventRegister

import com.example.data.models.EventFormFieldModel
import com.example.data.models.EventRegisterPrefilledFields
import com.example.data.models.EventRegisterProfilePrefilledFields.Companion.prefFromJson
import com.example.extensions.fromJson
import com.google.gson.JsonElement

class EventRegisterFieldPrefilled(
    field: EventFormFieldModel,
    result: JsonElement?
) : EventRegistrationData(field) {

    var value = result.prefFromJson(field)

    override fun isValid() = !field.isRequired || value?.isPrefilledFieldsValid() == true
    override fun hasForm() = value != null

}