package com.example.data.models

import android.util.Log
import com.example.extensions.calendar
import com.example.extensions.isSameDay
import com.example.extensions.isSameMonth
import com.example.extensions.isYesterday
import com.example.extensions.parseToDate
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

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

    val sessionDate: String?
        get() {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault())
            val startDate = (sessionStart ?: sessionEnd)?.parseToDate(format)?.calendar() ?: return null

            val defaultDate = startDate.get(Calendar.DAY_OF_MONTH).toString() + "." +
                    startDate.get(Calendar.MONTH).toString() + "." + startDate.get(Calendar.YEAR)

            val today = System.currentTimeMillis().calendar()

            return if (startDate.isSameMonth(today)) {
                if (startDate.isSameDay(today)) "сегодня"
                else if (startDate.isYesterday(today)) "вчера"
                else defaultDate
            } else defaultDate
        }
}

data class UserSessionBinds(
    @SerializedName("user")
    val user: UserDetail
)