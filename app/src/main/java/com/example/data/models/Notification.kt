package com.example.data.models

data class Notification(
        val id: Int = -1,
        val user_id: Int = -1,
        val code: String? = null,
        val type: String? = null,
        val time: String? = null,
        val text: String? = null,
        val status: String? = null

)