package com.example.data.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class QrAuthResponse(
    @SerializedName("ip")
    val mIPAddress: String,
    @SerializedName("device")
    val mDevice: String,
    @SerializedName("time")
    val mTime: String,
    @SerializedName("timestamp")
    val mTimeStamp : Date

)