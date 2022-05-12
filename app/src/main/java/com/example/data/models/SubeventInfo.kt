package com.example.data.models

import com.google.gson.annotations.SerializedName

data class SubeventInfo(
        val id: Int,
        val title: String,
        val description: String?,
        val location: String?,
        val start: String,
        val finish: String,
        @SerializedName("for_everyone")
        val isForEveryone: Boolean,
        @SerializedName("is_visitor")
        val isVisitor: Boolean,
        @SerializedName("is_speaker")
        val isSpeaker: Boolean,
        @SerializedName("is_in_calendar")
        val isInCalendar: Boolean,
        val groups: List<Tag.Group>,
        val tags: List<Tag.EventTag>,
        val speakers: List<Speaker>,
        val auditoriums: List<Auditorium>,
        @SerializedName("in_favorites")
        var isInFavorites: Boolean
)