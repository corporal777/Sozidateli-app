package com.examle.data.source.room.dto

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.examle.data.models.event.MemberModel
import com.examle.data.source.room.converter.EventMemberConverter

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