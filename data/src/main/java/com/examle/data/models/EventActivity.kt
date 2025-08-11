package com.examle.data.models

import com.examle.data.models.event.EventActivityModel
import com.google.gson.annotations.SerializedName

data class EventActivity(
    val activities: List<EventActivityModel>,
    @SerializedName("available_dates")
        val dates: List<EventDate>,
    @SerializedName("available_tags")
        val tags: List<Tag.EventTag>,
    @SerializedName("available_groups")
        val groups: List<Tag.Group>
)

data class EventDate(
    val date: String,
    val count: Int
)