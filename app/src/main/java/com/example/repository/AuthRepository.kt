package com.example.repository

import com.example.data.models.User
import io.reactivex.Completable

interface AuthRepository{

    fun authSN():Completable

    fun authEmail():Completable

    fun register():Completable
}