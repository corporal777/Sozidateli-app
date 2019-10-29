package com.example.data.models

data class EventRegisterForm(
        val type_of_fields: List<String>?,
        val fields: List<EventRegisterField>?,
        val groups: List<EventGroup>?,
        val moderate_registration: Boolean,
        val user_is_verified: Boolean
)