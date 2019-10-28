package com.example.data.models

sealed class RegisterEventFieldData<T>(
        val field: RegisterEventField,
        var value: T?
) {

    abstract fun isValid(): kotlin.Boolean

    class String(field: RegisterEventField, value: kotlin.String?) : RegisterEventFieldData<kotlin.String>(field, value) {
        override fun isValid() = !field.required || !value.isNullOrEmpty()
    }

    class Boolean(field: RegisterEventField, value: kotlin.Boolean?) : RegisterEventFieldData<kotlin.Boolean>(field, value) {
        override fun isValid() = !field.required || value != null
    }

    class Passport(field: RegisterEventField, value: EventPassport?) : RegisterEventFieldData<EventPassport>(field, value) {
        override fun isValid(): kotlin.Boolean {
            val passport = value
            return !field.required || passport != null && passport.isDataComplete()
        }
    }

    class Date(field: RegisterEventField, value: kotlin.String?) : RegisterEventFieldData<kotlin.String>(field, value) {
        override fun isValid() = !field.required || !value.isNullOrEmpty()
    }

    class SelectBox(field: RegisterEventField, value: kotlin.String?) : RegisterEventFieldData<kotlin.String>(field, value) {
        override fun isValid() = !field.required || !value.isNullOrEmpty()
    }

    class RadioBox(field: RegisterEventField, value: kotlin.String?) : RegisterEventFieldData<kotlin.String>(field, value) {
        override fun isValid() = !field.required || !value.isNullOrEmpty()
    }

    class Checkbox(field: RegisterEventField, value: Set<kotlin.String>?) : RegisterEventFieldData<Set<kotlin.String>>(field, value) {
        override fun isValid() = !field.required || !value.isNullOrEmpty()
    }

    class File(field: RegisterEventField, value: EventFile?) : RegisterEventFieldData<EventFile?>(field, value) {
        override fun isValid(): kotlin.Boolean {
            val file = value
            return !field.required || file != null
                    && file.name.isNotEmpty()
        }
    }
}