package com.example.data.models

sealed class RegisterEventFieldData<T>(
        val field: RegisterEventField,
        var value: T?
) {

    class String(field: RegisterEventField, value: kotlin.String?) : RegisterEventFieldData<kotlin.String>(field, value)
    class Boolean(field: RegisterEventField, value: kotlin.Boolean?) : RegisterEventFieldData<kotlin.Boolean>(field, value)
    class Passport(field: RegisterEventField, value: EventPassport?) : RegisterEventFieldData<EventPassport>(field, value)
    class Date(field: RegisterEventField, value: kotlin.String?) : RegisterEventFieldData<kotlin.String>(field, value)
    class SelectBox(field: RegisterEventField, value: kotlin.String?) : RegisterEventFieldData<kotlin.String>(field, value)
    class RadioBox(field: RegisterEventField, value: kotlin.String?) : RegisterEventFieldData<kotlin.String>(field, value)
    class Checkbox(field: RegisterEventField, value: Set<kotlin.String>?) : RegisterEventFieldData<Set<kotlin.String>>(field, value)
    class File(field: RegisterEventField, value: Document?) : RegisterEventFieldData<Document>(field, value)
}