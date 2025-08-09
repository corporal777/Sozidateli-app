package com.examle.data.models

import com.google.gson.annotations.SerializedName

data class ApiNewResponse<T>(
        val data: T,
        @SerializedName("totalCount")
        val totalCount: Int? = null,
        @SerializedName("unreadMessagesTotalCount")
        val unreadMessagesTotalCount: Int? = null
)