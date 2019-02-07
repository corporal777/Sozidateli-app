package com.example.repository

import com.example.data.models.Notification
import com.example.data.models.user.User
import io.reactivex.Single

interface UserRepository {
    fun getUser(): Single<User>
    fun getLastNotification(): Single<List<Notification>>
}