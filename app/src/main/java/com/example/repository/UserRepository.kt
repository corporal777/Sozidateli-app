package com.example.repository

import android.graphics.Bitmap
import com.example.data.models.*
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
    fun getNotifications(limit: Int, offset: Int): Maybe<PaginationResponse<RemoteNotification>>
    fun markNotificationsAsRead(ids: List<Int>): Maybe<MarkedResponse>
    fun getFcmToken(): Maybe<InstanceIdResult>
    fun notificationsRegister(token: String): Completable
    fun notificationsUnregister(token: String): Completable
    fun updateUser(data: Map<String, Any?>): Single<User>
    fun uploadAvatar(photo: Bitmap?): Single<User>
    fun uploadRecommendationFile(file: String): Single<User>
    fun getFavoriteUsers(limit: Int, offset: Int): Maybe<PaginationResponse<User>>

    fun changeEmailConfirm(email: String, code: String): Single<AuthResponse>

    fun getUserById(id: String): Maybe<User>

    fun addToFavorite(uid: String): Completable
    fun removeFromFavorite(uid: String): Completable

    fun getInterests(): Maybe<List<Interest>>
    fun searchUser(searchMap: Map<String, Any>, limit: Int, offset: Int): Maybe<PaginationResponse<User>>
}