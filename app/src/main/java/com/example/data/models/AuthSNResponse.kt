package com.example.data.models

data class AuthSNResponse(
        val user_email_not_set: Boolean,
        val user_id: Int,
        val user_email_confirmed: Boolean
)