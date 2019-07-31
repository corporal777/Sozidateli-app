package com.example.repository

import com.example.data.models.AuthResponse
import com.example.data.models.MarkedResponse
import com.example.data.models.Notification
import com.example.data.models.Speaker
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import com.google.firebase.iid.InstanceIdResult
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

interface UserRepository {
    fun getUserShort(): Maybe<User>
    fun getUserFull(): Maybe<User>
    fun getLastNotification(): Single<List<Notification>>
    fun getNotifications(limit: Int, offset: Int): Maybe<PaginationResponse<Notification>>
    fun markNotificationsAsRead(ids: List<Int>): Maybe<MarkedResponse>
    fun getFcmToken(): Maybe<InstanceIdResult>
    fun notificationsRegister(token: String): Completable
    fun notificationsUnregister(token: String): Completable
    fun updateUser(user: Map<String, Any?>): Single<User>
    fun uploadAvatar(photo: String?): Completable
    fun uploadRecommendationFile(file: String): Single<User>
    fun getFavoriteSpeakers(limit: Int, offset: Int): Maybe<PaginationResponse<Speaker>>

    fun changeEmailConfirm(email: String, code: String): Single<AuthResponse>

    fun getUserById(id: String): Single<User>
}