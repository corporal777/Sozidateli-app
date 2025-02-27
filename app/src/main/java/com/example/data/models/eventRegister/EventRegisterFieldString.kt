package com.example.data.models.eventRegister

import com.example.data.models.EventFormFieldModel
import com.example.extensions.fromJson
import com.example.util.AuthValidateUtil
import com.example.util.Utils
import com.google.gson.JsonElement

class EventRegisterFieldString(
    field: EventFormFieldModel,
    result: JsonElement?
) : EventRegistrationData(field) {

    var value = result.fromJson<String>()

    override fun isValid(): Boolean {
        return when (field.type) {
            EventFormFieldModel.Type.EMAIL -> {
                if (field.isRequired || !value.isNullOrEmpty()) Utils.isEmailValid(value)
                else true
            }

            EventFormFieldModel.Type.SITE -> {
                if (field.isRequired || !value.isNullOrEmpty())
                    AuthValidateUtil.isValidSite(value)
                else true
            }

            else -> if (field.isRequired) !value.isNullOrEmpty() else true
        }

    }

    override fun hasForm() = !value.isNullOrEmpty()
}