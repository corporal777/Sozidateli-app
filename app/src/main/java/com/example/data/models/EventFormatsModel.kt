package com.example.data.models

import com.google.gson.annotations.SerializedName

data class EventFormatsModel(
     @SerializedName("totalCount")
     val totalCount: Int? = null,
     val data: List< NewEventFormat>? = null
)

data class NewEventFormat(
    val id: Int? = null,
    val name: String? = null,
    val order: Int? = null
)