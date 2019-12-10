package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
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
        @SerializedName("can_add_to_calendar")
        var canAddToCalendar: Boolean,
        @SerializedName("in_favorites")
        var isInFavorites: Boolean,
        val groups: List<Tag.Group>?,
        val tags: List<Tag.EventTag>?
) : Parcelable