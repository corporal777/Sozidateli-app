package com.examle.data.source.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.examle.data.models.UserQrImage
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface UserQrImageDao : BaseDao<UserQrImage> {

    @Query("SELECT * FROM UserQrImage WHERE imageId= :id")
    fun getById(id: String): Single<UserQrImage>

    @Query("SELECT * FROM UserQrImage WHERE imageName= :name")
    fun getByName(name: String): Single<UserQrImage>

    @Query("DELETE FROM UserQrImage")
    fun deleteAll() : Completable
}