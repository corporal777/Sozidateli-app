package com.example.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.data.database.converters.UserEventConverter

@Entity
@TypeConverters(UserEventConverter::class)
data class UserEvent(
        @PrimaryKey(autoGenerate = false)
        val eventId: Int,
        val eventInfo: EventInfo,
        val subEvents: List<SubEvent>,
        val updatedAt: Long
) {

    @Ignore
    var isDataFromLocalStorage: Boolean = false
}