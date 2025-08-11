package com.examle.data.source.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.examle.data.source.room.dto.UserEvent
import io.reactivex.Single

@Dao
interface UserEventDao : BaseDao<UserEvent> {

    @Query("SELECT * FROM UserEvent WHERE eventId= :id")
    fun getById(id: String): Single<UserEvent>
}