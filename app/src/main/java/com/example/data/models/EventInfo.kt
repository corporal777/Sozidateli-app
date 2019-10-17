package com.example.data.models

import com.google.gson.annotations.SerializedName

data class EventInfo(
        val event: EventData,
        @SerializedName("place")
        val places: List<Partner>,
        val partners: List<Partner>,
        val pages: List<EventPage>
)