package com.examle.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.data.database.converters.UserEventConverter

@Entity
@TypeConverters(UserEventConverter::class)
data class UserEvent(
    @PrimaryKey(autoGenerate = false)
        val eventId: String,
    val eventInfo: EventResponse,
    val activity: EventActivity,
    val updatedAt: Long
) {

    @Ignore
    var isDataFromLocalStorage: Boolean = false

    fun isHasBuildingScheme(): Boolean {
        return !eventInfo.binds?.destinationScheme.isNullOrEmpty()
    }
}