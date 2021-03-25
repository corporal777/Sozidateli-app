package com.example.data.models

import com.google.gson.annotations.SerializedName

data class ApiNewResponse<T>(
        val data: T,
        @SerializedName("totalCount")
        val totalCount: Int? = null
)