package com.example.data.models

import com.google.gson.annotations.SerializedName

data class UserSessions(
    @SerializedName("currentSession")
    val currentSession : UserSessionModel,
    @SerializedName("sessions")
    val userSessions: List<UserSessionModel>
)

data class UserSessionModel(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("sessionId")
    val sessionId: Int,
    @SerializedName("sessionUid")
    val sessionUid: String,
    @SerializedName("sessionStart")
    val sessionStart: String,
    @SerializedName("sessionEnd")
    val sessionEnd: String,
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

)

data class UserSessionBinds(
    @SerializedName("user")
    val user: UserDetail
)