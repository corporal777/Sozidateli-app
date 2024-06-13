package com.example.repository

import android.graphics.Bitmap
import android.util.Log
import com.example.api.Api
import com.example.data.AppData
import com.example.data.bodies.*
import com.example.data.models.*
import com.example.ui.notification.NotificationType
import com.example.util.pagination.NotificationsResponse
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.example.extensions.toBodyPart
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject


class UserRepositoryImp
@Inject constructor(
    private val api: Api,
    private val appData: AppData
) : ApiRepository(appData), UserRepository {

    override fun getUserInternal(): Maybe<UserDetail> {
        return Maybe.zip(
            api.getUserById(appData.getId(), emptyList()),
            api.checkUserProfile(appData.getId().toString()).doOnSuccess { appData.checkUserState(it.fields) },
            BiFunction<UserDetail, UserProfileFieldsModel, UserDetail> { user, _ ->
                return@BiFunction user
            })
    }

    override fun getUserShortData(): Maybe<UserDetail> =
        api.getUserById(
            appData.getId(),
            arrayListOf(
                "rights",
                "education",
                "academic-degree",
                "work-experience",
                "recommendation-file",
                "organization",
                "userOrganizationRights",
                "external-invite-pgrf",
                "chat-room-with-me",
                "is-user-in-ban",
                "sessions-count",
                "device-sessions-count"
            )
        ).map { it }.doOnSuccess { appData.setAllUserInfo(it) }

    override fun getUserFullData(): Maybe<UserDetail> =
        Maybe.zip(
            getUserShortData(),
            api.checkUserProfile(appData.getId().toString()).doOnSuccess { appData.checkUserState(it.fields) },
            BiFunction<UserDetail, UserProfileFieldsModel, UserDetail> { user, _ ->
                return@BiFunction user
            })


    override fun getUserById(id: String): Maybe<UserDetail> = api.getUserById(
        id.toInt(),
        arrayListOf(
            "rights",
            "education",
            "academic-degree",
            "work-experience",
            "recommendation-file",
            "organization",
            "userOrganizationRights",
            "userFavorite",
            "chat-room-with-me",
            "is-user-in-ban"
        )
    ).map { it }

    override fun getUserByExternalId(name: String): Maybe<UserDetail> {
        return api.getUserByShortName(
            name,
            arrayListOf(
                "rights",
                "education",
                "academic-degree",
                "work-experience",
                "recommendation-file",
                "organization",
                "userOrganizationRights",
                "userFavorite",
                "chat-room-with-me",
                "is-user-in-ban"
            )
        )
    }

    override fun getUserByShortName(name: String): Maybe<UserDetail> {
        return api.getUserByShortName(name, emptyList())
    }

    override fun checkUserProfileSingle(): Single<UserProfileFieldsModel> =
        api.checkUserProfileSingle(appData.getId().toString()).doOnSuccess { state ->
            appData.checkUserState(state.fields)
        }

    override fun updateUserProfile(id: Int, map: Map<String, Any?>): Single<UserDetail> =
        api.updateProfile(id, map).doOnSuccess {
            appData.setUserShort(it)
        }

    override fun updateUserProfileField(map: Map<String, Any?>): Single<UserDetail> {
        return api.updateProfile(appData.getId(), map)
    }

    override fun updateProfile(id: Int, map: Map<String, Any?>): Single<UserDetail> =
        Single.zip(
            updateUserProfile(id, map),
            checkUserProfileSingle(),
            BiFunction<UserDetail, UserProfileFieldsModel, UserDetail> { user, _ ->
                return@BiFunction user
            })


    override fun searchAddress(query: String?): Single<SearchAddressModel> =
        api.searchAddress(query, 20)


    override fun getAllUsersSessions(deviceId: String): Maybe<UserSessions> =
        api.getAllUsersSessions()

    override fun getAllUsersSessionsFromCurrentDevice(deviceId: String): Maybe<UserSessions> =
        api.getAllUsersSessionsFromCurrentDevice(deviceId, "user")

    override fun deleteUsersDeviceSession(id: Int): Completable =
        api.deleteUsersDeviceSession(id)

    override fun killAllUsersOtherSessions(): Completable = api.killAllUsersOtherSessions()

    override fun killUsersDeviceSession(id: Int): Completable = api.killUsersDeviceSession(id)

//    override fun getFcmToken(): Maybe<InstanceIdResult> {
//        return Maybe.create { emitter ->
//            RxHandler.assignOnTask(emitter, FirebaseInstanceId.getInstance().instanceId)
//        }
//    }

    override fun notificationsInviteAccept(id: Int): Completable {
//        return call(api.notificationsInviteAccept(id)).doOnSuccess {
//            appData.notificationsCount = it.unreadCount
//            appData.notificationReadSubject.onNext(id to Notification.AcceptState.ACCEPTED)
//        }.ignoreElement()
        return Completable.complete()
    }

    override fun notificationsInviteDecline(id: Int): Completable {
//        return call(api.notificationsInviteDecline(id)).doOnSuccess {
//            appData.notificationsCount = it.unreadCount
//            appData.notificationReadSubject.onNext(id to Notification.AcceptState.CANCELED)
//        }.ignoreElement()
        return Completable.complete()
    }

    /*override fun uploadAvatar(photo: Bitmap?): Single<User> {
        return call(api.uploadAvatar(photo?.com.example.extensions.toBodyPart("file", "image.png")))
    }*/

    override fun changeUserImage(photo: Bitmap?): Single<ImageModel> {
        return api.changeUserImage(appData.getId(), photo?.toBodyPart("file", "image.png"))
            .map { it.toImageModel() }
    }

    override fun deleteImage(): Completable {
        return api.deleteImage(appData.getId())
    }

    /*override fun uploadRecommendationFile(file: String, mimeType: String): Single<User> {
        return call(api.uploadDocument(
                file.let {
                    val imageFile = File(it)
                    val body = imageFile.asRequestBody(mimeType.toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("file[0]", imageFile.name, body)
                }))
    }*/

    override fun uploadRecommendedFile(body: List<MultipartBody.Part?>): Single<ImageModel> {
        return api.uploadRecommendedFile(body)
    }

    override fun changeRecommendedFile(
        fileId: Int,
        body: List<MultipartBody.Part?>
    ): Single<ImageModel> {
        return api.changeRecommendedFile(fileId, body)
    }

    //+
    override fun changeRecommendedFiles(body: RequestBody): Single<List<FileModel>> {
        return api.changeRecommendedFiles(appData.getId(), body).map { it.data }
    }

    override fun deleteRecommendedFile(fileId: Int): Completable {
        return api.deleteRecommendedFile(fileId)
    }

    /*override fun getFavoriteUsers(limit: Int, offset: Int): Maybe<PaginationResponse<User?>> {
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
    }*/

    /*override fun sendStatusPhoneConfirmSms(password: String): Completable {
        return call(api.sendStatusPhoneConfirmSms(password))
    }

    override fun sendStatusPhoneConfirmCode(code: String): Completable {
        return call(api.sendStatusPhoneConfirmCode(code))
    }*/

    override fun deleteProfile(id: Int): Completable {
        return api.deleteProfile(id)
    }

    override fun deleteProfile(): Completable {
        return api.deleteProfile(appData.getId())
    }

    override fun getEventCalendar(data: EventsCalendarListBody): Maybe<EventsListModel> =
        api.getEventCalendar(data.toMap())

    override fun logout(id: Int): Completable = api.logout(id)

    override fun changePassword(id: Int, body: PasswordBody): Completable =
        api.changePassword(id, body)

    override fun checkPassword(password: String): Completable {
        return api.checkPassword(appData.getId(), password)
    }

    override fun updateWorkExperience(body: WorkExperienceServerModel): Single<WorkExperienceServerModel> =
        api.updateWorkExperience(appData.getId(), body).doOnSuccess {
            appData.updateWorkExperience(it)
        }

    override fun getInterestsList(ids: List<Int>?): Maybe<InterestsModel> =
        api.getInterestsList(200, ids)

    override fun getEducationLevel(): Single<EducationLevelModel> {
        val education = appData.educationLevels
        return if (!education.isNullOrEmpty()) Single.just(EducationLevelModel(education, 6))
        else api.getEducationLevel().doOnSuccess { appData.educationLevels.addAll(it.data ?: emptyList()) }
    }

    override fun getSpeciality(): Single<EducationLevelModel> {
        val speciality = appData.specialities
        return if (!speciality.isNullOrEmpty()) Single.just(EducationLevelModel(speciality, 23))
        else api.getSpeciality(100).doOnSuccess { appData.specialities.addAll(it.data ?: emptyList()) }
    }

    override fun getAcademicDegrees(): Single<EducationLevelModel> {
        val academicDegrees = appData.academicDegrees
        return if (!academicDegrees.isNullOrEmpty()) Single.just(EducationLevelModel(academicDegrees, 4))
        else api.getAcademicDegrees().doOnSuccess { appData.academicDegrees.addAll(it.data?: emptyList()) }
    }

    override fun getUserProfileAdditionalData(): Completable {
        return getEducationLevel().flatMap { getSpeciality() }.flatMap { getAcademicDegrees() }
            .ignoreElement()
    }

    private fun sendUserEducation(body: EducationBodyModel): Single<EducationBodyModel> =
        api.updateUserEducation(appData.getId(), body)
            .doOnSuccess {
                appData.updateUserEducation(it.data)
            }

    private fun sendUserAcademicDegree(body: AcademicDegreeBodyModel): Single<AcademicDegreeBodyModel> =
        api.updateUserAcademicDegree(appData.getId(), body)
            .doOnSuccess {
                appData.updateUserAcademicDegree(it.data)
            }

    override fun updateUserEducation(
        educationLevel: ToggleIntModel?,
        educationsList: List<EducationModel>?,
        degree: List<AcademicDegreeModel>?
    ): Single<String> {
        return Single.zip(sendUserEducation(EducationBodyModel(educationsList)),
            sendUserAcademicDegree(AcademicDegreeBodyModel(degree)),
            updateUserProfile(
                appData.getId(),
                mapOf(UserDetail.USER_EDUCATION_LEVEL to educationLevel)
            ),
            Function3<EducationBodyModel, AcademicDegreeBodyModel, UserDetail, String> { _, _, _ ->
                return@Function3 ""
            })
    }

    override fun getNotificationsList(map: Map<String, Any>): Maybe<PaginationResponse<Notification>> {
        return api.getNotifications(map)
            .map {
                PaginationResponse(
                    it.totalCount,
                    it.data.map {
                        Notification.fromRemoteNotification(it)
                    }
                )
            }
    }

    override fun getUserNotifications(map: Map<String, Any>): Maybe<NotificationsResponse<Notification>> {
        return api.getUserNotifications(map).map {
            Log.e("SIZE", it.data.size.toString())
            Log.e("TOTAL", it.totalCount.toString())
            NotificationsResponse(
                it.totalCount,
                it.data.map { Notification.fromRemoteNotification(it) },
                it.totalUnreadInvites,
                it.totalUnread,
                it.allUnread,
                it.activeInvites,
                it.archiveInvites
            )
        }
    }

    override fun getInAppList(): Maybe<List<NotificationModel>> {
        return api.getNotifications(mapOf(
            NotificationModel.NOTIFICATION_LIMIT to 50,
            NotificationModel.NOTIFICATION_USER to appData.getId(),
            NotificationModel.NOTIFICATION_IS_IN_APP to true,
            NotificationModel.NOTIFICATION_ACKNOWLEDGED to false
        )).map { it.data }
    }

    override fun getNotificationNotReadedSize(map: Map<String, Any>): Maybe<Int> {
        return api.getNotifications(map)
            .map { it.totalCount }
    }

    /*override fun getNotFilledFields(): Maybe<List<NotFilledFields>> {
        return call(api.getNotFilledFields())
    }*/

    override fun getUsers(map: Map<String, Any>): Maybe<PaginationResponse<UserDetail?>> {
        return api.getUsers(map)
            .map {
                PaginationResponse(
                    it.totalCount,
                    it.data ?: arrayListOf()
                )
            }
    }

    override fun getUsersWithoutPagination(map: Map<String, Any>): Maybe<List<UserDetail>> {
        return api.getUsers(map)
            .map { it.data }
    }

    override fun getUsersFavoritesList(map: Map<String, Any>): Maybe<PaginationResponse<UserDetail?>> {
        return api.getUsersFavoritesList(map)
            .map {
                it.data.forEach { org ->
                    org.entity?.model?.binds =
                        UserBinds(userFavorite = EventUserFavorite(org.id?.toLong(), org.user))
                }
                PaginationResponse(it.totalCount, it.data.mapNotNull { org -> org.entity?.model })
            }
    }

    override fun getUsersFavoritesWithoutPagination(map: Map<String, Any>): Maybe<List<UserDetail?>> {
        return api.getUsersFavoritesList(map)
            .map {
                it.data.forEach { org ->
                    org.entity?.model?.binds =
                        UserBinds(userFavorite = EventUserFavorite(org.id?.toLong(), org.user))
                }
                it.data.map { org -> org.entity?.model }
            }
    }

    override fun getNotificationDetail(
        notificationId: String,
        loadModel: Boolean
    ): Single<NotificationModel> = api.getNotificationDetail(notificationId, loadModel)

    override fun markAsRead(notificationId: String): Completable = api.markAsRead(notificationId)

    override fun markAllNotificationsAsRead(type: NotificationType?): Maybe<UnacceptedInviteNotification> {
        return when (type) {
            NotificationType.PROJECTS -> {
                api.markAllTypeNotificationsAsRead(appData.getId().toString(), "pgrf")
            }

            NotificationType.ORGANIZER -> {
                api.markAllTypeNotificationsAsRead(appData.getId().toString(), "org")
            }

            NotificationType.ESTIMATES -> {
                api.markAllTypeNotificationsAsRead(appData.getId().toString(), "evaluate")
            }

            NotificationType.EVENTS -> {
                api.markAllTypeNotificationsAsRead(appData.getId().toString(), "event")
            }

            NotificationType.SYSTEM -> {
                api.markAllTypeNotificationsAsRead(appData.getId().toString(), "system")
            }

            else -> api.markAllNotificationsAsRead(appData.getId().toString())
        }

    }


    override fun approveOrgMember(orgMemberId: String, body: ApproveBody): Completable =
        api.approveOrgMember(orgMemberId, body)

    override fun declineOrgMember(orgMemberId: String, body: DeclineBody): Completable =
        api.declineOrgMember(orgMemberId, body)

    override fun approvePgrf(pgrfId: String): Completable =
        api.approvePgrf(pgrfId)

    override fun declinePgrf(pgrfId: String): Completable =
        api.declinePgrf(pgrfId)

    override fun approveAssistance(assistanceId: String): Completable =
        api.approveAssistance(assistanceId)

    override fun declineAssistance(assistanceId: String): Completable =
        api.declineAssistance(assistanceId)

    override fun getAssistanceInviteDetail(assistanceId: String): Single<InviteDetail> =
        api.getInviteAssistanceDetail(assistanceId)

    override fun cancelEventMember(evMemberId: String, body: CancelBody): Completable =
        api.cancelEvMember(evMemberId, body)

    override fun approveEventMember(memberId: String): Completable {
        return api.approveEventMember(memberId)
    }

    override fun declineEventMember(memberId: String): Completable {
        return api.declineEventMember(memberId)
    }

    override fun checkEmailPhone(email: String?, phone: String?): Completable =
        api.checkEmailPhone(email, phone)


    override fun searchUsers(map: Map<String, Any>): Maybe<PaginationResponse<UserDetail?>> {
        return api.searchGlobal(map)
            .map { PaginationResponse(it.users.count, it.users.data) }
        //.map { it.users }
    }

    override fun bindSocialAccount(uuid: String, socialType: String, isRebind : Boolean): Maybe<SnBindDataModel> {
        return api.bindSocialAccount(
            BindSocialAccountBody(
                uuid,
                appData.getId(),
                socialType,
                isRebind
            )
        )
    }

    override fun unbindSocialAccount(uuid: String, socialType: String): Completable {
        return api.unBindSocialAccount(mapOf("uuid" to uuid, "socialNetwork" to socialType))
    }

    override fun getFcmToken(): Maybe<String> {
        return Maybe.create { emitter ->
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    emitter.onSuccess(token)
                }
                .addOnFailureListener { e -> emitter.onError(e) }
        }
    }
}