package com.example.data.models

data class RegisterFieldsData(
        val type_of_fields: List<String>?,
        val fields: List<RegisterEventField>?,
        val groups: List<Category>,
        val moderate_registration: Boolean,
        val user_is_verified: Boolean
)