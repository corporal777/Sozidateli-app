package com.example.repository

import android.graphics.Bitmap
import com.example.api.Api
import com.example.api.NewApi
import com.example.data.AppData
import com.example.data.bodies.*
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
import io.reactivex.functions.Function3
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import toBodyPart
import java.io.File
import javax.inject.Inject

class UserRepositoryImp
@Inject constructor(
        private val api: Api,
        private val newApi: NewApi,
        private val appData: AppData
) : ApiRepository(appData), UserRepository {

    override fun getUserShortNew(): Maybe<UserDetail> = newApi.getUserShort(appData.getId(),
            arrayListOf("rights", "education", "academic-degree", "work-experience", "recommendation-file", "organization", "userOrganizationRights")).map { it }.doOnSuccess {
        appData.setAllUserInfo(it)
    }

    override fun updateProfile(id: Int, map: Map<String, Any?>): Single<UserDetail> =
            newApi.updateProfile(id, map).doOnSuccess {
                appData.setUserShortNew(it)
            }

    override fun confirmEmailCode(id: Int, body: EmailCodeBody): Single<ConfirmEmail> =
            newApi.confirmEmailCode(id, body).doOnSuccess {
                appData.login(it.token)
                appData.saveId(it.id)
            }

    override fun getUserShort(): Maybe<UserShort> = call(api.getUserShort()).doOnSuccess {
        appData.setUserShort(it)
    }

    override fun confirmPhoneCode(id: Int, body: PhoneCodeBody): Completable =
            newApi.confirmPhoneCode(id, body)

    override fun sendPhoneCode(id: Int, phone: String): Completable =
            newApi.sendPhoneCode(id, phone)

    override fun getAddress(body: AddressBody): Maybe<List<AddressResponse>> =  call(api.getAddress(body)).doOnSuccess {

    }

    override fun searchAddress(query: String?): Single<SearchAddressModel> =
            newApi.searchAddress(query, 20)

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

    override fun changeUserImage(photo: Bitmap?): Single<ImageModel> {
        return newApi.changeUserImage(appData.getId(), photo?.toBodyPart("file", "image.png"))
    }

    override fun deleteImage(): Completable {
        return newApi.deleteImage(appData.getId())
    }

    override fun uploadRecommendationFile(file: String, mimeType: String): Single<User> {
        return call(api.uploadDocument(
                file.let {
                    val imageFile = File(it)
                    val body = imageFile.asRequestBody(mimeType.toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("file[0]", imageFile.name, body)
                }))
    }

    override fun uploadRecommendedFile(body: List<MultipartBody.Part?>): Single<ImageModel> {
        return newApi.uploadRecommendedFile(body)
    }

    override fun changeRecommendedFile(fileId: Int, body: List<MultipartBody.Part?>): Single<ImageModel> {
        return newApi.changeRecommendedFile(fileId, body)
    }

    override fun deleteRecommendedFile(fileId: Int): Completable {
        return newApi.deleteRecommendedFile(fileId)
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

    override fun sendStatusPhoneConfirmCode(code: String): Completable {
        return call(api.sendStatusPhoneConfirmCode(code))
    }

    override fun userEventCalendar(): Maybe<List<UserEventCalendar>> {
        return call(api.eventCalendar())
    }

    override fun setUserAtEvent(events: List<Int>, atEvent: List<Boolean>, lat: Double, lon: Double): Completable {
        return call(api.setUserAtEvent(events, atEvent, lat, lon))
    }

    override fun deleteProfile(id: Int): Completable {
        return /*call(api.deleteProfile())*/newApi.deleteProfile(id)
    }

    override fun deleteProfile(): Completable {
        return newApi.deleteProfile(appData.getId())
    }

    override fun getEventCalendar(data: EventsCalendarListBody): Maybe<EventsListModel> =
            newApi.getEventCalendar(data.toMap())

    override fun logout(id: Int): Completable = newApi.logout(id)

    override fun changePassword(id: Int, body: PasswordBody): Completable = newApi.changePassword(id, body)

    override fun checkIfPasswordValid(password: String): Completable =
            newApi.checkIfPasswordValid(appData.getId(), password)

    override fun updateWorkExperience(body: WorkExperienceServerModel): Single<WorkExperienceServerModel> =
            newApi.updateWorkExperience(appData.getId(), body).doOnSuccess {
                appData.updateWorkExperience(it)
            }

    override fun getInterestsList(ids: List<Int>?): Maybe<InterestsModel> =
            newApi.getInterestsList(200, ids)

    override fun getEducationLevel(): Single<EducationLevelModel> {
        val education = appData.getUserNew().educationLevelList
        return if (education != null) {
            Single.just(EducationLevelModel(education, 6))
        } else {
            newApi.getEducationLevel()
                    .doOnSuccess {
                        appData.updateEducationLevel(it.data)
                    }
        }
    }

    override fun getSpeciality(): Single<EducationLevelModel> {
        val speciality = appData.getUserNew().speciality
        return if (speciality != null) {
            Single.just(EducationLevelModel(speciality, 23))
        } else {
            newApi.getSpeciality(100)
                    .doOnSuccess {
                        appData.updateSpeciality(it.data)
                    }
        }
    }

    override fun getAcademicDegrees(): Single<EducationLevelModel> {
        val academicDegrees = appData.getUserNew().academicDegrees
        return if (academicDegrees != null) {
            Single.just(EducationLevelModel(academicDegrees, 4))
        } else {
            newApi.getAcademicDegrees()
                    .doOnSuccess {
                        appData.updateAcademicDegrees(it.data)
                    }
        }
    }

    override fun updateUserEducation(body: EducationBodyModel): Single<EducationBodyModel> =
            newApi.updateUserEducation(appData.getId(), body)
                    .doOnSuccess {
                        appData.updateUserEducation(it.data)
                    }

    override fun updateUserAcademicDegree(body: AcademicDegreeBodyModel): Single<AcademicDegreeBodyModel> =
            newApi.updateUserAcademicDegree(appData.getId(), body)
                    .doOnSuccess {
                        appData.updateUserAcademicDegree(it.data)
                    }

    override fun updateUserEducationScreen(educationLevel: Int?, educationsList: List<EducationModel>?, degree: List<AcademicDegreeModel>?): Single<String> {
        return Single.zip(updateUserEducation(EducationBodyModel(educationsList)),
                updateUserAcademicDegree(AcademicDegreeBodyModel(degree)),
                updateProfile(appData.getId(), mapOf(UserDetail.USER_EDUCATION_LEVEL to educationLevel)),
                Function3<EducationBodyModel, AcademicDegreeBodyModel, UserDetail, String> { t1, t2, t3 ->
                    return@Function3 ""
        })
    }

    override fun getNotFilledFields(): Maybe<List<NotFilledFields>> {
        return call(api.getNotFilledFields())
    }
}