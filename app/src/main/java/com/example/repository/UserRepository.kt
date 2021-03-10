package com.example.repository

import android.graphics.Bitmap
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import com.google.firebase.iid.InstanceIdResult
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import retrofit2.http.Body

interface UserRepository {
    fun getUserShort(): Maybe<UserShort>
    fun getUserFull(): Maybe<User>
    fun getLastNotification(): Single<List<Notification>>
    fun getNotifications(limit: Int, offset: Int): Maybe<PaginationResponse<RemoteNotification>>
    fun getNotification(id: Int): Maybe<RemoteNotification>
    fun markNotificationsAsRead(ids: List<Int>): Completable
    fun notificationsInviteAccept(id: Int): Completable
    fun notificationsInviteDecline(id: Int): Completable
    fun getFcmToken(): Maybe<InstanceIdResult>
    fun notificationsRegister(token: String): Completable
    fun notificationsUnregister(token: String): Completable
    fun updateUser(data: Map<String, Any?>): Single<User>
    fun uploadAvatar(photo: Bitmap?): Single<User>
    fun uploadRecommendationFile(file: String, mimeType: String): Single<User>
    fun getFavoriteUsers(limit: Int, offset: Int): Maybe<PaginationResponse<User?>>

    fun changeEmailConfirm(email: String, code: String): Single<AuthResponse>

    fun getUserById(id: String): Maybe<User>

    fun addToFavorite(uid: String): Completable
    fun removeFromFavorite(uid: String): Completable

    fun searchUser(searchMap: Map<String, Any>, limit: Int, offset: Int): Maybe<PaginationResponse<User>>
    fun usersList(limit: Int, offset: Int, filter: Map<String, Any>): Maybe<PaginationResponse<User?>>
    fun checkPassword(password: String): Completable
    fun sendStatusPhoneConfirmSms(password: String): Completable
    fun sendStatusPhoneConfirmCode(code: String): Completable

    fun userEventCalendar(): Maybe<List<UserEventCalendar>>
    fun setUserAtEvent(events: List<Int>, atEvent: List<Boolean>, lat: Double, lon: Double): Completable

    fun deleteProfile(): Completable
    fun getAddress(body: AddressBody): Maybe<List<AddressResponse>>
}