package com.example.data.models

import com.google.gson.annotations.SerializedName

data class EventInfo(
        val event: EventData,
        @SerializedName("place")
        val places: List<Place>,
        val partners: List<EventParther>,
        val pages: List<EventPage>,
        @SerializedName("user_registration")
        val userRegistration: EventUserRegistration?
)