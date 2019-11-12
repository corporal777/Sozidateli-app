package com.example.data.models

import com.google.gson.annotations.SerializedName

data class SubEvent(
        val id: String,
        val title: String,
        val start: String,
        val finish: String,
        @SerializedName("is_visitor")
        val isVisitor: Boolean,
        @SerializedName("for_everyone")
        val forEveryone: Boolean,
        @SerializedName("in_calendar")
        var isInCalendar: Boolean,
        val groups: List<Tag.Group>,
        val tags: List<Tag.EventTag>
)