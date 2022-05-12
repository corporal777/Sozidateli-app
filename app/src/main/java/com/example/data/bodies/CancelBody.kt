package com.example.data.bodies

import com.google.gson.annotations.SerializedName

data class CancelBody(
        @SerializedName("canceledBy")
        val canceledBy: Int
)