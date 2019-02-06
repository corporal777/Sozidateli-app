package com.example.ui.snAuth

data class SnAuth(
        val token: String? = null,
        val email: String? = null,
        val snType: SnAuthActivity.SnType
)