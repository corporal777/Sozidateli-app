package com.example.data.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class QrAuthResponse(
    @SerializedName("ip")
    val ipAddress: String,
    @SerializedName("device")
    val device: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("timestamp")
    val timeStamp : Date

)