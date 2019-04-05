package com.example.data.models

import com.google.gson.annotations.SerializedName

data class SubEvent(
        val id: Int,
        val title: String,
        val start: String,
        val finish: String,
        val for_everyone: Boolean,
        @SerializedName("is_visitor")
        val isVisitor: Boolean,
        @SerializedName("is_speaker")
        val isSpeaker: Boolean,
        @SerializedName("is_in_calendar")
        val isInCalendar: Boolean,
        val categories: List<EventCategory>,
        val tags: List<EventTag>
)