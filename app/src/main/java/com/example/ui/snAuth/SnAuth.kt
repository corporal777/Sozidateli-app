package com.example.ui.snAuth

data class SnAuth(
        val token: String,
        val email: String? = null,
        val snType: SnType
)