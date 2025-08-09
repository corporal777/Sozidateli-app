package com.examle.data.bodies

import com.google.gson.annotations.SerializedName

data class DeclineBody(
        @SerializedName("declinedBy")
        val declinedBy: Int
)