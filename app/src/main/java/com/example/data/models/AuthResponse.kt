package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class AuthResponse(
    val id: Int? = null,
    val token: String? = null
) : Parcelable

data class SnAuthResponse(
    val accessData: AuthResponse?,
    val personalData: SnUserData?
)

data class QrAuthResponse(
    @SerializedName("ip")
    val ipAddress: String,
    val device: String,
    val time: String,
    @SerializedName("timestamp")
    val timeStamp: Date
)

data class TempAuthResponse(
    val id: Int? = null,
    val token: String? = null,
    val type: String? = null
)