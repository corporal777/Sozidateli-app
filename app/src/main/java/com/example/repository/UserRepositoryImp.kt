package com.example.repository

import android.graphics.Bitmap
import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.data.models.user.User.Companion.FIELD_USER_IS_IN_FAVORITE
import com.example.util.pagination.PaginationResponse
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import durdinapps.rxfirebase2.RxHandler
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import toBodyPart
import java.io.File
import javax.inject.Inject

class UserRepositoryImp
@Inject constructor(
        private val api: Api,
        private val appData: AppData
) : ApiRepository(appData), UserRepository {

    override fun getUserShort(): Maybe<UserShort> = call(api.getUserShort()).doOnSuccess {
        appData.setUserShort(it)
    }

    override fun getUserFull(): Maybe<User> = call(api.getUserFull()).doOnSuccess { appData.setUser(it) }

    override fun getLastNotification() = call(api.getLastNotification())

    override fun getNotifications(limit: Int, offset: Int): Maybe<PaginationResponse<RemoteNotification>> {
        return callPagination(api.getUserNotifications(limit, offset))
    }

    override fun getNotification(id: Int): Maybe<RemoteNotification> {
        return call(api.getUserNotification(id))
    }

    override fun markNotificationsAsRead(ids: List<Int>): Completable {
        return call(api.markNotificationsAsRead(ids)).doOnSuccess {
            appData.notificationsCount = it.unreadCount
            ids.forEach { id -> appData.notificationReadSubject.onNext(id to Notification.AcceptState.NONE) }
        }.ignoreElement()
    }

    override fun getFcmToken(): Maybe<InstanceIdResult> {
        return Maybe.create { emitter ->
            RxHandler.assignOnTask(emitter, FirebaseInstanceId.getInstance().instanceId)
        }
    }

    override fun notificationsRegister(token: String): Completable {
        return call(api.notificationsRegister(token))
    }

    override fun notificationsUnregister(token: String): Completable {
        return call(api.notificationsUnregister(token))
    }

    override fun notificationsInviteAccept(id: Int): Completable {
        return call(api.notificationsInviteAccept(id)).doOnSuccess {
            appData.notificationsCount = it.unreadCount
            appData.notificationReadSubject.onNext(id to Notification.AcceptState.ACCEPTED)
        }.ignoreElement()
    }

    override fun notificationsInviteDecline(id: Int): Completable {
        return call(api.notificationsInviteDecline(id)).doOnSuccess {
            appData.notificationsCount = it.unreadCount
            appData.notificationReadSubject.onNext(id to Notification.AcceptState.CANCELED)
        }.ignoreElement()
    }

    override fun updateUser(data: Map<String, Any?>) = call(api.updateUser(data))

    override fun uploadAvatar(photo: Bitmap?): Single<User> {
        return call(api.uploadAvatar(photo?.toBodyPart("file", "image.png")))
    }

    override fun uploadRecommendationFile(file: String): Single<User> {
        return call(api.uploadDocument(
                file.let {
                    val imageFile = File(it)
                    val body = imageFile.asRequestBody("application/pdf".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("file[0]", imageFile.name, body)
                }))
    }

    override fun getFavoriteUsers(limit: Int, offset: Int): Maybe<PaginationResponse<User?>> {
        return usersList(limit, offset, mapOf(FIELD_USER_IS_IN_FAVORITE to true))
    }

    override fun changeEmailConfirm(email: String, code: String): Single<AuthResponse> {
        return call(api.changeEmailConfirm(email, code))
    }

    override fun getUserById(id: String): Maybe<User> {
        return call(api.getUserById(id))
    }

    override fun addToFavorite(uid: String): Completable = call(api.userAddToFavorite(uid))

    override fun removeFromFavorite(uid: String): Completable = call(api.userRemoveFromFavorite(uid))

    override fun searchUser(searchMap: Map<String, Any>, limit: Int, offset: Int): Maybe<PaginationResponse<User>> {
        return callPagination(api.chatSearch(searchMap, limit, offset))
    }

    override fun usersList(limit: Int, offset: Int, filter: Map<String, Any>): Maybe<PaginationResponse<User?>> {
        return callPagination(api.getUsersList(limit, offset, filter))
    }

    override fun checkPassword(password: String): Completable {
        return call(api.checkPassword(password))
    }

    override fun sendStatusPhoneConfirmSms(password: String): Completable {
        return call(api.sendStatusPhoneConfirmSms(password))
    }

    override fun sendStatusPhoneConfirmCode(password: String): Completable {
        return call(api.sendStatusPhoneConfirmCode(password))
    }

    override fun userEventCalendar(): Maybe<List<UserEventCalendar>> {
        return call(api.eventCalendar())
    }

    override fun setUserAtEvent(events: List<Int>, atEvent: List<Boolean>, lat: Double, lon: Double): Completable {
        return call(api.setUserAtEvent(events, atEvent, lat, lon))
    }
}