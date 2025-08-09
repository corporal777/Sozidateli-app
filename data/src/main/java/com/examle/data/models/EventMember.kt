package com.examle.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.data.database.converters.EventMemberConverter

@Entity
@TypeConverters(EventMemberConverter::class)
data class EventMember(
    @PrimaryKey(autoGenerate = false)
    val eventId: String,
    val members: List<MemberModel>,
    val updatedAt: Long
) {

    @Ignore
    var isDataFromLocalStorage: Boolean = false
}