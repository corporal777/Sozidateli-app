package com.example.data.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class AuthResponse(
    val id: Int? = null,
    val token: String? = null,
    val errors: List<Errors>? = null
)

data class SnAuthResponse(
    val accessData : AuthResponse?,
    val personalData : SnUserData?
)

data class QrAuthResponse(
    @SerializedName("ip")
    val ipAddress: String,
    val device: String,
    val time: String,
    @SerializedName("timestamp")
    val timeStamp : Date
)

data class TempAuthResponse(
    val id: Int? = null,
    val token: String? = null,
    val type: String? = null
)