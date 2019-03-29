package com.example.data.models

import com.google.gson.annotations.SerializedName

data class EventInfo(
        val event: Event,
        val tags: List<EventTag>,
        val categories: List<EventCategory>,
        @SerializedName("activities_dates")
        val dates: List<EventDate>,
        @SerializedName("news_count")
        val newsCount: Int,
        @SerializedName("docs_count")
        val docsCount: Int
)