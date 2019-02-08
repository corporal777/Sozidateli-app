package com.example.repository

import com.example.data.models.Notification
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import com.google.firebase.iid.InstanceIdResult
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

interface UserRepository {
    fun getUser(): Maybe<User>
    fun getLastNotification(): Single<List<Notification>>
    fun getNotifications(limit: Int, offset: Int): Maybe<PaginationResponse<Notification>>
    fun getFcmToken(): Maybe<InstanceIdResult>
    fun notificationsRegister(token: String): Completable
    fun notificationsUnregister(token: String): Completable
    fun updateUser(user: User): Single<User>
}