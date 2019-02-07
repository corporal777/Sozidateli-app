package com.example.data.models

import com.google.gson.annotations.SerializedName

data class Session(
        @SerializedName("is-auth")
        val isAuth: Boolean,
        @SerializedName("is-start-session")
        val isStartSession: Boolean,
        val token: String,
        @SerializedName("user-id")
        val userId: Int
)