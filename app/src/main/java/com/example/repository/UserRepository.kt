package com.example.repository

import android.graphics.Bitmap
import com.example.data.bodies.*
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import com.google.firebase.iid.InstanceIdResult
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.Query
import okhttp3.MultipartBody
import retrofit2.http.*

interface UserRepository {
    fun getUserShortNew(): Maybe<UserDetail>
    fun getUserShortData(): Maybe<UserDetail>
    fun getUserByIdNew(id: String): Maybe<UserDetail>
    //fun getUserShort(): Maybe<UserShort>
    //fun getUserFull(): Maybe<User>
    //fun getLastNotification(): Single<List<Notification>>
    //fun getNotifications(limit: Int, offset: Int): Maybe<PaginationResponse<RemoteNotification>>
    //fun getNotification(id: Int): Maybe<RemoteNotification>
    //fun markNotificationsAsRead(ids: List<Int>): Completable
    fun notificationsInviteAccept(id: Int): Completable
    fun notificationsInviteDecline(id: Int): Completable
    fun getFcmToken(): Maybe<InstanceIdResult>
    fun notificationsRegister(token: String): Completable
    fun notificationsUnregister(token: String): Completable
    fun updateUser(data: Map<String, Any?>): Single<User>
    //fun uploadAvatar(photo: Bitmap?): Single<User>
    //fun uploadRecommendationFile(file: String, mimeType: String): Single<User>
    //fun getFavoriteUsers(limit: Int, offset: Int): Maybe<PaginationResponse<User?>>

    //fun changeEmailConfirm(email: String, code: String): Single<AuthResponse>

    //fun getUserById(id: String): Maybe<User>

    //fun addToFavorite(uid: String): Completable
    //fun removeFromFavorite(uid: String): Completable

    //fun searchUser(searchMap: Map<String, Any>, limit: Int, offset: Int): Maybe<PaginationResponse<User>>
    //fun usersList(limit: Int, offset: Int, filter: Map<String, Any>): Maybe<PaginationResponse<User?>>
    //fun checkPassword(password: String): Completable
    fun checkPasswordNew(password: String): Completable
    //fun sendStatusPhoneConfirmSms(password: String): Completable
    //fun sendStatusPhoneConfirmCode(code: String): Completable

    fun userEventCalendar(): Maybe<List<UserEventCalendar>>
    fun setUserAtEvent(events: List<Int>, atEvent: List<Boolean>, lat: Double, lon: Double): Completable

    fun deleteProfile(id: Int): Completable
    fun updateProfile(id: Int, map: Map<String, Any?>): Single<UserDetail>
    fun updateUserProfile(id: Int, map: Map<String, Any?>): Single<UserDetail>
    fun confirmEmailCode(id: Int, body: EmailCodeBody): Single<ConfirmEmail>
    fun confirmPhoneCode(id: Int, body: PhoneCodeBody): Completable
    fun sendPhoneCode(id: Int, phone: String): Completable

    fun getEventCalendar(data: EventsCalendarListBody): Maybe<EventsListModel>
    fun logout(id: Int): Completable
    fun changePassword(id: Int, body: PasswordBody): Completable
    fun changeUserImage(photo: Bitmap?): Single<ImageModel>
    fun deleteImage(): Completable
    fun uploadRecommendedFile(body: List<MultipartBody.Part?>): Single<ImageModel>
    fun changeRecommendedFile(fileId: Int, body: List<MultipartBody.Part?>): Single<ImageModel>
    fun deleteRecommendedFile(fileId : Int): Completable
    //fun getAddress(body: AddressBody): Maybe<List<AddressResponse>>
    fun deleteProfile(): Completable
    fun checkIfPasswordValid(password: String): Completable

    fun updateWorkExperience(body: WorkExperienceServerModel): Single<WorkExperienceServerModel>
    fun getInterestsList(ids: List<Int>?): Maybe<InterestsModel>
    fun getEducationLevel(): Single<EducationLevelModel>
    fun getSpeciality(): Single<EducationLevelModel>
    fun getAcademicDegrees(): Single<EducationLevelModel>
    fun updateUserEducation(body: EducationBodyModel): Single<EducationBodyModel>
    fun updateUserAcademicDegree(body: AcademicDegreeBodyModel): Single<AcademicDegreeBodyModel>
    fun updateUserEducationScreen(educationLevel: Int?, educationsList: List<EducationModel>?, degree: List<AcademicDegreeModel>?): Single<String>
    //fun getNotFilledFields(): Maybe<List<NotFilledFields>>
    fun searchAddress(query: String?): Single<SearchAddressModel>
    fun getNotificationsList(map: Map<String, Any>): Maybe<PaginationResponse<Notification>>
    fun getNotificationNotReadedSize(map: Map<String, Any>): Maybe<Int>
    fun getUsers(map: Map<String, Any>): Maybe<PaginationResponse<UserDetail?>>
    fun getUsersWithoutPagination(map: Map<String, Any>): Maybe<List<UserDetail?>>
    fun getUsersFavoritesList(map: Map<String, Any>): Maybe<PaginationResponse<UserDetail?>>
    fun getUsersFavoritesWithoutPagination(map: Map<String, Any>): Maybe<List<UserDetail?>>
    fun unblockUser(id : Int): Completable
    fun blockUser(id : Int): Completable
    fun checkUserProfile(): Maybe<UserProfileFieldsModel>
    fun checkUserProfileSingle(): Single<UserProfileFieldsModel>
    fun getNotificationDetail(notificationId: String, loadModel: Boolean): Single<NotificationModel>
    fun markAsRead(notificationId: String): Completable
    fun approveOrgMember(orgMemberId: String, body: ApproveBody): Completable
    fun declineOrgMember(orgMemberId: String, body: DeclineBody): Completable
    fun approvePgrf(pgrfId: String): Completable
    fun declinePgrf(pgrfId: String): Completable
    fun approveAssistance(assistanceId: String): Completable
    fun declineAssistance(assistanceId: String): Completable
    fun cancelEvMember(evMemberId: String, body: CancelBody): Completable
    fun checkEmailPhone(email: String?, phone: String?): Completable
}