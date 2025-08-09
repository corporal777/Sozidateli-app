package com.examle.data.models

import com.google.gson.annotations.SerializedName

data class UserStatusProtectionRequires(
        @SerializedName("LOW_PROTECTION")
        val low: List<String>,
        @SerializedName("MID_PROTECTION")
        val mid: List<String>,
        @SerializedName("MAX_PROTECTION")
        val max: List<String>
)