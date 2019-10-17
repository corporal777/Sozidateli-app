package com.example.data.models

import com.google.gson.annotations.SerializedName

data class EventPage(
        val id: Int,
        @SerializedName("event_id")
        val event: String,
        val menu: String,
        val sort: Int
)