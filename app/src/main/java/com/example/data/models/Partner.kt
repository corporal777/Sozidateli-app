package com.example.data.models

import com.google.gson.annotations.SerializedName

data class Partner(
        val id: String,
        @SerializedName("event_id")
        val eventId: String,
        val name: String,
        val description: String?,
        @SerializedName("type_support")
        val typeSupport: String?,
        val web: String?,
        val logo: String?,
        @SerializedName("bg_image")
        val background: String?
)