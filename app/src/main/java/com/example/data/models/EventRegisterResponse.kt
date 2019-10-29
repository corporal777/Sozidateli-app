package com.example.data.models

import com.google.gson.annotations.SerializedName

data class EventRegisterResponse(
        val id: String,
        @SerializedName("event_id")
        val eventId: String,
        val event: EventRegistration,
        @SerializedName("custom_fields")
        val fields: List<EventRegisterResponseField?>?,
        val group_id: String?,
        val group: EventGroup?,
        @SerializedName("user_id")
        val userId: String?,
        val created: String?,
        val status: String?
)
