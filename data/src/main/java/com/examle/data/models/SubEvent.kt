package com.examle.data.models

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
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SubEvent) return false

        if (id != other.id) return false
        if (title != other.title) return false
        if (start != other.start) return false
        if (finish != other.finish) return false
        if (isVisitor != other.isVisitor) return false
        if (forEveryone != other.forEveryone) return false
        if (isInCalendar != other.isInCalendar) return false
        if (canAddToCalendar != other.canAddToCalendar) return false
        if (isInFavorites != other.isInFavorites) return false
        if (groups != other.groups) return false
        if (tags != other.tags) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + start.hashCode()
        result = 31 * result + finish.hashCode()
        result = 31 * result + isVisitor.hashCode()
        result = 31 * result + forEveryone.hashCode()
        result = 31 * result + isInCalendar.hashCode()
        result = 31 * result + canAddToCalendar.hashCode()
        result = 31 * result + isInFavorites.hashCode()
        result = 31 * result + (groups?.hashCode() ?: 0)
        result = 31 * result + (tags?.hashCode() ?: 0)
        return result
    }
}