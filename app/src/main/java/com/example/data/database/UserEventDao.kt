package com.example.data.database

import androidx.room.Dao
import androidx.room.Query
import com.example.data.models.UserEvent
import io.reactivex.Maybe

@Dao
interface UserEventDao : BaseDao<UserEvent> {

    @Query("SELECT * FROM UserEvent WHERE eventId= :id")
    fun getById(id: Int): Maybe<UserEvent>
}