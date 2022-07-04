package com.example.data.database

import androidx.room.Dao
import androidx.room.Query
import com.example.data.models.EventMember
import com.example.data.models.UserEvent
import io.reactivex.Single

@Dao
interface EventMemberDao : BaseDao<EventMember> {

    @Query("SELECT * FROM EventMember WHERE eventId= :id")
    fun getById(id: String): Single<EventMember>
}