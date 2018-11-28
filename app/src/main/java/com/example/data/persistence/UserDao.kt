package com.example.data.persistence

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.example.data.BaseDao
import com.example.data.models.User
import io.reactivex.Single

@Dao
interface UserDao : BaseDao<User> {

    @Query("SELECT * FROM User")
    fun getAll(): Single<List<User>>

    @Query("DELETE FROM User")
    fun deleteAll()
}