package com.example.data.models.eventRegister

import com.example.data.models.EventFormFieldModel

sealed class EventRegistrationData(val field: EventFormFieldModel) {

    abstract fun isValid(): Boolean
    abstract fun hasForm(): Boolean
}