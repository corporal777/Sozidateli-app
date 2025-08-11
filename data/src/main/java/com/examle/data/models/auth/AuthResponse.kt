package com.examle.data.models.auth

import android.os.Parcelable
import com.examle.domain.model.user.SnUserData
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class AuthResponse(
    val id: Int,
    val token: String
) : Parcelable


data class QrAuthResponse(
    @SerializedName("ip")
    val ipAddress: String,
    val device: String,
    val time: String,
    @SerializedName("timestamp")
    val timeStamp: Date
)
