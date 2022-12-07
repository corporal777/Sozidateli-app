package com.example.data.models

import com.example.ui.event.registration.items.ProfileFieldsFormModel
import com.example.ui.event.registration.items.ProfileFieldsFormResult

sealed class EventRegisterFieldData<T>(
    val field: EventRegisterField,
    var value: T?
) {

    abstract fun isValid(): kotlin.Boolean
    abstract fun hasForm(): kotlin.Boolean

    class Prefilled(
        field: EventRegisterField,
        value: ProfileFieldsFormResult?,
    ) :
        EventRegisterFieldData<ProfileFieldsFormResult>(field, value) {
        override fun isValid() =
            if (value != null) value!!.checkProfileFieldsIsValid(value!!) else true

        override fun hasForm() = value != null
    }

    class String(field: EventRegisterField, value: kotlin.String?) :
        EventRegisterFieldData<kotlin.String>(field, value) {
        override fun isValid() = !field.required || !value.isNullOrEmpty()
        override fun hasForm() = !value.isNullOrEmpty()
    }

    class Boolean(field: EventRegisterField, value: kotlin.Boolean?) :
        EventRegisterFieldData<kotlin.Boolean>(field, value) {
        override fun isValid() = !field.required || value != null
        override fun hasForm() = value != null
    }

    class Passport(field: EventRegisterField, value: EventPassport?) :
        EventRegisterFieldData<EventPassport>(field, value) {
        override fun isValid(): kotlin.Boolean {
            val passport = value
            return !field.required || passport != null && passport.isDataComplete()
        }

        override fun hasForm(): kotlin.Boolean {
            val passport = value
            return passport != null
        }
    }

    class Date(field: EventRegisterField, value: kotlin.String?) :
        EventRegisterFieldData<kotlin.String>(field, value) {
        override fun isValid() = !field.required || !value.isNullOrEmpty()
        override fun hasForm() = !value.isNullOrEmpty()
    }

    class SelectBox(field: EventRegisterField, value: kotlin.String?) :
        EventRegisterFieldData<kotlin.String>(field, value) {
        override fun isValid() = !field.required || !value.isNullOrEmpty()
        override fun hasForm() = !value.isNullOrEmpty()
    }

    class RadioBox(field: EventRegisterField, value: kotlin.String?) :
        EventRegisterFieldData<kotlin.String>(field, value) {
        override fun isValid() = !field.required || !value.isNullOrEmpty()
        override fun hasForm() = !value.isNullOrEmpty()
    }

    class Checkbox(field: EventRegisterField, value: Set<kotlin.String>?) :
        EventRegisterFieldData<Set<kotlin.String>>(field, value) {
        override fun isValid() = !field.required || !value.isNullOrEmpty()
        override fun hasForm() = !value.isNullOrEmpty()
    }

    class File(field: EventRegisterField, value: EventFile?) :
        EventRegisterFieldData<EventFile?>(field, value) {
        override fun isValid(): kotlin.Boolean {
            val file = value
            return !field.required || file != null
                    && file.name.isNotEmpty()
        }

        override fun hasForm(): kotlin.Boolean {
            val file = value
            return file != null && file.name.isNotEmpty()
        }
    }
}