package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import durdinapps.rxfirebase2.RxHandler
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class UserRepositoryImp
@Inject constructor(
        private val api: Api,
        private val appData: AppData
) : ApiRepository(appData), UserRepository {

    override fun getUserShort(): Maybe<User> = call(api.getUserShort()).doOnSuccess { appData.setUser(it) }
    override fun getUserFull(): Maybe<User> = call(api.getUserFull()).doOnSuccess { appData.setUser(it) }

    override fun getLastNotification() = call(api.getLastNotification())

    override fun getNotifications(limit: Int, offset: Int): Maybe<PaginationResponse<Notification>> {
        return callPagination(api.getUserNotifications(limit, offset))
    }

    override fun markNotificationsAsRead(ids: List<Int>): Maybe<MarkedResponse> {
        return call(api.markNotificationsAsRead(ids))
    }

    override fun getFcmToken(): Maybe<InstanceIdResult> {
        return Maybe.create<InstanceIdResult> { emitter ->
            RxHandler.assignOnTask(emitter, FirebaseInstanceId.getInstance().instanceId)
        }
    }

    override fun notificationsRegister(token: String): Completable {
        return call(api.notificationsRegister(token))
    }

    override fun notificationsUnregister(token: String): Completable {
        return call(api.notificationsUnregister(token))
    }

    override fun updateUser(map: Map<String, Any?>) = call(api.updateUser(map)
            .doOnSuccess { appData.setUser(it.response) })

    override fun uploadAvatar(photo: String?): Completable {
        if (photo.isNullOrEmpty()) return Completable.complete()
        return call(api.uploadAvatar(
                photo.let {
                    val imageFile = File(it)
                    val body = RequestBody.create(MediaType.parse("image/*"), imageFile)
                    MultipartBody.Part.createFormData("file", imageFile.name, body)
                }))
    }

    override fun uploadRecommendationFile(file: String): Single<User> {
        return call(api.uploadDocument(
                file.let {
                    val imageFile = File(it)
                    val body = RequestBody.create(MediaType.parse("application/pdf"), imageFile)
                    MultipartBody.Part.createFormData("file[0]", imageFile.name, body)
                }))
    }

    override fun getFavoriteUsers(limit: Int, offset: Int): Maybe<PaginationResponse<User>> {
        return callPagination(api.getUserFavoriteUsers(limit, offset))
    }

    override fun changeEmailConfirm(email: String, code: String): Single<AuthResponse> {
        return call(api.changeEmailConfirm(email, code))
    }

    override fun getUserById(id: String): Maybe<User> {
        return call(api.getUserById(id))
    }

    override fun addToFavorite(uid: String): Completable = call(api.userAddToFavorite(uid))

    override fun removeFromFavorite(uid: String): Completable = call(api.userRemoveFromFavorite(uid))

    override fun getInterests(): Maybe<List<Interest>> {
        return call(api.getInterestsList())
    }
}