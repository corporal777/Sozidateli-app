package com.example.data.models

import com.google.gson.annotations.SerializedName

data class EventActivity(
        val activities: List</*SubEvent*/EventActivityModel>,
        /*@SerializedName("available_dates")
        val dates: List<EventDate>,
        @SerializedName("available_tags")
        val tags: List<Tag.EventTag>,
        @SerializedName("available_groups")
        val groups: List<Tag.Group>
        val activities: List<SubEvent>,*/
        @SerializedName("available_dates")
        val dates: List<EventDate>,
        @SerializedName("available_tags")
        val tags: List<Tag.EventTag>,
        @SerializedName("available_groups")
        val groups: List<Tag.Group>
)