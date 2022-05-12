package com.example.data.database

import androidx.room.Dao
import androidx.room.Query
import com.example.data.models.UserEvent
import io.reactivex.Single

@Dao
interface UserEventDao : BaseDao<UserEvent> {

    @Query("SELECT * FROM UserEvent WHERE eventId= :id")
    fun getById(id: String): Single<UserEvent>
}