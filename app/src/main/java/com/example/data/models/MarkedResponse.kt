package com.example.data.models

import com.google.gson.annotations.SerializedName

data class MarkedResponse(
        @SerializedName("count_marked")
        val countMarked: Int
)