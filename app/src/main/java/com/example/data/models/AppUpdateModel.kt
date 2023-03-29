package com.example.data.models

import com.google.gson.annotations.SerializedName

data class AppUpdateModel(
    val status: UpdateStatus,
    var isAvailable : Boolean = true
) {
    enum class UpdateStatus {
        @SerializedName("actual")
        ACTUAL,

        @SerializedName("deprecated")
        DEPRECATED,

        @SerializedName("unsupported")
        UNSUPPORTED
    }

    fun hasUpdate(): Boolean {
        return status == UpdateStatus.UNSUPPORTED || status == UpdateStatus.DEPRECATED
    }

    fun isUpdateRequired(): Boolean {
        return status == UpdateStatus.UNSUPPORTED
    }
}