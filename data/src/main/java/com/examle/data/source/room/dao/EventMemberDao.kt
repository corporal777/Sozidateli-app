package com.examle.data.source.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.examle.data.source.room.dto.EventMember
import io.reactivex.Single

@Dao
interface EventMemberDao : BaseDao<EventMember> {

    @Query("SELECT * FROM EventMember WHERE eventId= :id")
    fun getById(id: String): Single<EventMember>
}