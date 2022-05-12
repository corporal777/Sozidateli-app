package com.example.data.models

import com.google.gson.annotations.SerializedName

data class ResponseDetail(
        @SerializedName("use_offset")
        val offset: Int,
        @SerializedName("use_limit")
        val limit: Int,
        @SerializedName("items_total")
        val total: Int,
        @SerializedName("items_outputed")
        val itemsOutputted: Int
)