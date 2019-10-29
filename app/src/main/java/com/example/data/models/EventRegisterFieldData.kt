package com.example.data.models

sealed class EventRegisterFieldData<T>(
        val field: EventRegisterField,
        var value: T?
) {

    abstract fun isValid(): kotlin.Boolean

    class String(field: EventRegisterField, value: kotlin.String?) : EventRegisterFieldData<kotlin.String>(field, value) {
        override fun isValid() = !field.required || !value.isNullOrEmpty()
    }

    class Boolean(field: EventRegisterField, value: kotlin.Boolean?) : EventRegisterFieldData<kotlin.Boolean>(field, value) {
        override fun isValid() = !field.required || value != null
    }

    class Passport(field: EventRegisterField, value: EventPassport?) : EventRegisterFieldData<EventPassport>(field, value) {
        override fun isValid(): kotlin.Boolean {
            val passport = value
            return !field.required || passport != null && passport.isDataComplete()
        }
    }

    class Date(field: EventRegisterField, value: kotlin.String?) : EventRegisterFieldData<kotlin.String>(field, value) {
        override fun isValid() = !field.required || !value.isNullOrEmpty()
    }

    class SelectBox(field: EventRegisterField, value: kotlin.String?) : EventRegisterFieldData<kotlin.String>(field, value) {
        override fun isValid() = !field.required || !value.isNullOrEmpty()
    }

    class RadioBox(field: EventRegisterField, value: kotlin.String?) : EventRegisterFieldData<kotlin.String>(field, value) {
        override fun isValid() = !field.required || !value.isNullOrEmpty()
    }

    class Checkbox(field: EventRegisterField, value: Set<kotlin.String>?) : EventRegisterFieldData<Set<kotlin.String>>(field, value) {
        override fun isValid() = !field.required || !value.isNullOrEmpty()
    }

    class File(field: EventRegisterField, value: EventFile?) : EventRegisterFieldData<EventFile?>(field, value) {
        override fun isValid(): kotlin.Boolean {
            val file = value
            return !field.required || file != null
                    && file.name.isNotEmpty()
        }
    }
}