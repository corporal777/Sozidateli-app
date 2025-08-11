package com.examle.domain.repository

import com.examle.domain.model.user.EducationLevelModel
import com.examle.domain.model.user.UserProfileStateModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun checkUserProfileState(): Flow<UserProfileStateModel>

    fun getEducationLevel(): Flow<EducationLevelModel>
    fun getSpeciality(): Flow<EducationLevelModel>
    fun getAcademicDegrees(): Flow<EducationLevelModel>

//    fun getUserInternal(): Maybe<UserDetail>
//    fun getUserFullData(): Maybe<UserDetail>
//    fun getUserShortData(): Maybe<UserDetail>
//    fun getUserById(id: String): Maybe<UserDetail>
//    fun getUserByShortName(name: String, withData : Boolean): Maybe<UserDetail>
//
//    fun notificationsInviteAccept(id: Int): Completable
//    fun notificationsInviteDecline(id: Int): Completable
//
//
//    fun changePassword(body: PasswordBody): Completable
//    fun checkPassword(password: String): Completable
//
//    fun deleteProfile(id: Int): Completable
//    fun updateProfile(id: Int, map: Map<String, Any?>): Single<UserDetail>
//    fun updateUserProfile(id: Int, map: Map<String, Any?>): Single<UserDetail>
//    fun updateUserProfileField(map: Map<String, Any?>): Single<UserDetail>
//
//
//    fun getAllUsersSessions(deviceId: String): Maybe<UserSessions>
//    fun getAllUsersSessionsFromCurrentDevice(deviceId : String): Maybe<UserSessions>
//    fun killAllUsersOtherSessions(): Completable
//    fun killUsersDeviceSession(id : Int): Completable
//    fun deleteUsersDeviceSession(id : Int): Completable
//
//    fun logout(id: Int): Completable
//    fun changeUserImage(photo: Bitmap?): Single<ImageModel>
//    fun deleteImage(): Completable
//    fun uploadRecommendedFile(body: List<MultipartBody.Part?>): Single<ImageModel>
//    fun changeRecommendedFile(fileId: Int, body: List<MultipartBody.Part?>): Single<ImageModel>
//    fun changeRecommendedFiles(body: RequestBody): Single<List<FileModel>>
//    fun deleteRecommendedFile(fileId : Int): Completable
//    //fun getAddress(body: AddressBody): Maybe<List<AddressResponse>>
//    fun deleteProfile(): Completable
//
//    fun updateWorkExperience(body: WorkExperienceServerModel): Single<WorkExperienceServerModel>
//    fun getInterestsList(ids: List<Int>?): Maybe<InterestsModel>

//    fun getUserProfileAdditionalData(): kotlinx.coroutines.flow.Flow<EducationLevelModel>
//
//    fun updateUserEducation(educationLevel: ToggleIntModel?, educationsList: List<EducationModel>?, degree: List<AcademicDegreeModel>?): Single<String>
//    fun searchAddress(query: String?): Single<SearchAddressModel>
//    fun getUserNotifications(map: Map<String, Any>): Maybe<NotificationsResponse<NotificationLocal>>
//    fun getNotificationsList(map: Map<String, Any>): Maybe<PaginationResponse<NotificationLocal>>
//    fun getInAppList(): Maybe<List<NotificationModel>>
//    fun getUsers(map: Map<String, Any>): Maybe<PaginationResponse<UserDetail>>
//    fun getUsersWithoutPagination(map: Map<String, Any>): Maybe<List<UserDetail>>
//    fun getUsersFavoritesList(map: Map<String, Any>): Maybe<PaginationResponse<UserDetail>>
//    fun getUsersFavoritesWithoutPagination(map: Map<String, Any>): Maybe<List<UserDetail?>>
//

//    fun checkUserProfileSingle(): Single<UserProfileFieldsModel>
//    fun getNotificationDetail(notificationId: String, loadModel: Boolean): Single<NotificationModel>
//    fun markAsRead(notificationId: String): Completable
//    fun approveOrgMember(orgMemberId: String): Completable
//    fun declineOrgMember(orgMemberId: String): Completable
//    fun approvePgrf(pgrfId: String): Completable
//    fun declinePgrf(pgrfId: String): Completable
//    fun approveAssistance(assistanceId: String): Completable
//    fun declineAssistance(assistanceId: String): Completable
//    fun getAssistanceInviteDetail(assistanceId: String): Single<InviteDetail>
//
//    fun cancelEventMember(evMemberId: String, body: com.examle.data.bodies.CancelBody): Completable
//    fun approveEventMember(memberId : String) : Completable
//    fun declineEventMember(memberId : String) : Completable
//
//
//    fun checkEmailPhone(email: String?, phone: String?): Completable
//
//    fun searchUsers(map: Map<String, Any>): Maybe<PaginationResponse<UserDetail>>
//
//    //+
//    fun bindSocialAccount(uuid : String, socialType : String, isRebind : Boolean): Maybe<SnBindDataModel>
//    fun unbindSocialAccount(uuid : String, socialType : String): Completable
//
//    //+
//    fun addOrRemoveUserFavorite(user: UserDetail) : Single<Optional<EventUserFavorite>>
}