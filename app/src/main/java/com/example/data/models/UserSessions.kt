package com.example.data.models

import android.util.Log
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class UserSessions(
    @SerializedName("currentSession")
    val currentSession: UserSessionModel,
    @SerializedName("sessions")
    val userSessions: List<UserSessionModel>
)

data class UserSessionModel(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("sessionId")
    val sessionId: Long,
    @SerializedName("sessionUid")
    val sessionUid: String,
    @SerializedName("sessionStart")
    val sessionStart: String?,
    @SerializedName("sessionEnd")
    val sessionEnd: String?,
    @SerializedName("ip")
    val ipAddress: String,
    @SerializedName("device")
    val device: String,
    @SerializedName("deviceId")
    val deviceId: String,
    @SerializedName("appVersion")
    val appVersion: String? = null,
    @SerializedName("appBuild")
    val appBuild: String? = null,
    @SerializedName("deviceModel")
    val deviceModel: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("isLogged")
    var isLogged: Boolean,
    @SerializedName("binds")
    val binds: UserSessionBinds

) {
    val deviceName: String
        get() = if (deviceModel.isNullOrEmpty() || deviceModel == "Device not defined") {
            "Устройство не определено"
        } else deviceModel

    val deviceType: String
        get() = if (device.isNullOrEmpty()) {
            if (deviceModel.contains("iphone", true)) "Созидатели iOS, IP $ipAddress"
            else "Устройство не определено"
        } else "$device, IP $ipAddress"

    val deviceLocation: String
        get() = if (location.isNullOrEmpty() || location.contains("Location not defined")) {
            "Местоположение не определено"
        } else location

    val deviceIp: String
        get() = if (!ipAddress.isNullOrEmpty()) ipAddress else "IP адрес не определен"

    val deviceNameWithVersion: String
        get() = if (!device.isNullOrEmpty()) {
            if (device.contains("iOS", true) || device.contains("android", true)) {
                if (!appVersion.isNullOrEmpty()) "$device $appVersion ($appBuild)" else device
            } else device
        } else {
            if (deviceModel.contains("iphone", true)) "Созидатели iOS"
            else "Устройство не определено"
        }

    val sessionDate: String
        get() {
            val format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
            val today = LocalDateTime.now()

            val date =
                if (!sessionStart.isNullOrEmpty()) LocalDateTime.parse(sessionStart, format)
                else if (!sessionEnd.isNullOrEmpty()) LocalDateTime.parse(sessionEnd, format)
                else today

            return if (today.monthValue == date.monthValue && today.dayOfMonth - date.dayOfMonth == 1) "вчера"
            else if (today.dayOfMonth == date.dayOfMonth && today.monthValue == date.monthValue) "сегодня"
            else date.dayOfMonth.toString() + "." + date.monthValue.toString() + "." + date.year
        }
}

data class UserSessionBinds(
    @SerializedName("user")
    val user: UserDetail
)