package com.example.api

import com.example.data.bodies.*
import com.example.data.models.*
import com.example.data.bodies.MessageBodyNew
import com.example.util.pagination.NotificationsResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

@JvmSuppressWildcards
interface Api {

    @GET("v1/user/temp/login")
    fun getTemporaryToken(): Single<TempAuthResponse>

    @POST("v1/user/login")
    fun authEmailOrPhone(@Body body: AuthBody): Single<AuthResponse>

    @POST("v1/user/accept-qr")
    fun authWithQrCode(@Body body: QrBody): Single<AuthResponse>

    //+
    @POST("v1/user/social/login")
    fun authWithVk(@Body body: VKAuthBody): Single<SnAuthResponse>

    @POST("v1/user/send-qr")
    fun sendQrCodeToGetDeviceInfo(@Body body: QrBody): Single<QrAuthResponse>

    @GET("v1/user/{id}")
    fun getUserById(@Path("id") id: Int, @Query("binds") binds: List<String>?): Maybe<UserDetail>

    //+
    @GET("v1/user/get-by-short-name")
    fun getUserByShortName(
        @Query("shortName") shortName: String,
        @Query("binds") binds: List<String>?
    ): Maybe<UserDetail>

    //+
    @POST("v1/user")
    fun registerUser(@Body body: RegisterBody): Single<AuthResponse>

    //+
    @POST("v1/user/social/register")
    fun registerSnUser(@Body body: SnRegisterBody): Single<AuthResponse>

    @PATCH("v1/user/{id}")
    fun updateProfile(@Path("id") id: Int, @Body map: Map<String, Any?>): Single<UserDetail>

    @GET("v1/user/{id}/email/confirm/send")
    fun registerEmailResend(@Path("id") id: Int, @Query("email") email: String): Completable

    @GET("v1/user/{id}/phone/confirm/send")
    fun registerPhoneResend(
        @Path("id") id: Int,
        @Query("type") type: String,
        @Query("phone") email: String
    ): Completable

    @POST("v1/user/{id}/phone/confirm")
    fun confirmPhoneCode(@Path("id") id: Int, @Body body: ConfirmCodeBody): Single<AuthResponse>

    @POST("v1/user/{id}/email/confirm")
    fun confirmEmailCode(@Path("id") id: Int, @Body body: EmailCodeBody): Completable

    //+
    @GET("v1/user/get-sessions")
    fun getAllUsersSessions(): Maybe<UserSessions>

    @GET("v1/user/get-device-sessions")
    fun getAllUsersSessionsFromCurrentDevice(
        @Query("deviceId") deviceId: String,
        @Query("binds") binds: String
    ): Maybe<UserSessions>

    @PATCH("v1/user/kill-sessions")
    fun killAllUsersOtherSessions(): Completable

    @PATCH("v1/user/kill-session/{id}")
    fun killUsersDeviceSession(@Path("id") id: Int): Completable

    @PATCH("v1/user/delete-session/{id}")
    fun deleteUsersDeviceSession(@Path("id") id: Int): Completable

    //+
    @GET("v1/event")
    fun getEventCalendar(@QueryMap map: Map<String, Any>): Maybe<EventsListModel>

    @POST("v1/user/{id}/logout")
    fun logout(@Path("id") id: Int): Completable

    @PATCH("v1/user/{id}/password")
    fun changePassword(@Path("id") id: Int, @Body body: PasswordBody): Completable

    @PATCH("v1/user/{id}/deactivate")
    fun deleteProfile(@Path("id") id: Int): Completable

    @Multipart
    @PATCH("v1/user/{id}/image")
    fun changeUserImage(
        @Path("id") id: Int,
        @Part image: MultipartBody.Part?
    ): Single<ImageResponse>

    @DELETE("v1/user/{id}/image")
    fun deleteImage(@Path("id") id: Int): Completable

    @Multipart
    @POST("v1/user-recommendation-file")
    fun uploadRecommendedFile(@Part body: List<MultipartBody.Part?>): Single<ImageModel>

    @Multipart
    @PATCH("v1/user-recommendation-file/{id}")
    fun changeRecommendedFile(
        @Path("id") fileId: Int,
        @Part body: List<MultipartBody.Part?>
    ): Single<ImageModel>

    //+
    @PATCH("v1/user-recommendation-file/user/{id}")
    fun changeRecommendedFiles(
        @Path("id") userId: Int,
        @Body body: RequestBody
    ): Single<UserFiles>

    @DELETE("v1/user-recommendation-file/{id}")
    fun deleteRecommendedFile(@Path("id") fileId: Int): Completable

    @GET("v1/interest")
    fun getInterestsList(
        @Query("limit") limit: Int,
        @Query("id") ids: List<Int>?
    ): Maybe<InterestsModel>

    @GET("v1/education-level")
    fun getEducationLevel(): Single<EducationLevelModel>

    @GET("v1/speciality")
    fun getSpeciality(@Query("limit") limit: Int): Single<EducationLevelModel>

    @GET("v1/academic-degree")
    fun getAcademicDegrees(): Single<EducationLevelModel>

    //user education
    @GET("v1/user-education")
    fun getUserEducations(@Query("user") id: Int): Single<EducationBodyModel>

    @PATCH("v1/user-education/user/{id}")
    fun updateUserEducation(
        @Path("id") id: Int,
        @Body body: EducationBodyModel
    ): Single<EducationBodyModel>

    @PATCH("v1/user-academic-degree/user/{id}")
    fun updateUserAcademicDegree(
        @Path("id") id: Int,
        @Body body: AcademicDegreeBodyModel
    ): Single<AcademicDegreeBodyModel>

    @PATCH("v1/user-work-experience/user/{id}")
    fun updateWorkExperience(
        @Path("id") id: Int,
        @Body body: WorkExperienceServerModel
    ): Single<WorkExperienceServerModel>

    //+
    @GET("v1/event")
    fun getEventsList(@QueryMap map: Map<String, Any>): Maybe<EventNewModel>

    //+
    @GET("v1/event/event-list")
    fun getEventsListNew(@QueryMap map: Map<String, Any>): Maybe<EventNewModel>

    //+
    @GET("v1/event/event-list")
    fun getOrganizationEventsList(@QueryMap map: Map<String, Any>): Maybe<EventNewModel>

    //+
    @GET("v1/event/sorted")
    fun getSortedEventsList(@QueryMap map: Map<String, Any>): Maybe<EventNewModel>

    //+
    @GET("v1/user-calendar/events")
    fun getUserCalendarEvents(@Query("binds") binds: String): Maybe<ApiNewResponse<List<EventNew>>>


    //+
    @GET("v1/organization/{id}")
    fun getOrganizationDetails(
        @Path("id") organizationId: String,
        @Query("binds") binds: String?
    ): Single<OrganizationNew>

    //+
    @GET("v1/event-format")
    fun getEventFormatsList(@QueryMap map: Map<String, Any>): Maybe<EventFormatsModel>

    @GET("v1/event-format/active")
    fun getActiveEventFormatsList(): Maybe<EventFormatsModel>

    //profile state
    @GET("v1/user/{id}/profile-state")
    fun checkUserProfile(@Path("id") id: Int): Maybe<UserProfileFieldsModel>

    @GET("v1/user/{id}/profile-state")
    fun checkUserProfileSingle(@Path("id") id: Int): Single<UserProfileFieldsModel>

    //+
    @GET("v1/event/{id}")
    fun getEventDetails(@Path("id") eventId: String, @Query("binds") binds: String): Maybe<EventNew>

    //+
    @GET("v1/event-member/{id}")
    fun getEventMember(
        @Path("id") memberId: String,
        @Query("binds") binds: String
    ): Maybe<MemberModel>

    //+
    @POST("v1/event-subscriptions")
    fun createEventSubscription(@Body body: EventSubscriptionRequest): Completable

    @DELETE("v1/event-subscriptions/{id}")
    fun deleteEventSubscription(@Path("id") eventId: Int): Completable

    //+
    @GET("v1/event-page/{id}")
    fun getPageDetails(@Path("id") pageId: String): Single<PageModel>

    //+
    @GET("v1/event-member")
    fun getSpeakers(@QueryMap map: Map<String, Any>): Maybe<ApiNewResponse<List<MemberModel>>>

    //+
    @GET("v1/event-partner/{id}")
    fun getPartnerDetails(
        @Path("id") partnerId: String,
        @Query("binds") binds: String
    ): Single<PartnerModel>

    //+
    @POST("v1/user-favorites")
    fun addToFavorite(@Body body: AddToFavoriteModel): Single<AddFavoriteModel>

    //+
    @POST("v1/user/temp/user-favorites")
    fun addToTempFavorite(@Body body: AddToFavoriteModel): Single<AddTempFavoriteModel>

    //+
    @DELETE("v1/user-favorites/{id}")
    fun deleteFromFavorite(@Path("id") id: String): Completable

    //+
    @DELETE("v1/user/temp/user-favorites/{id}")
    fun deleteFromTempFavorite(@Path("id") id: String): Completable

    @GET("v1/address/search")
    fun searchAddress(
        @Query("query") query: String?,
        @Query("limit") limit: Int
    ): Single<SearchAddressModel>

    @GET("v1/user/password/recover/send")
    fun sendEmailRecovery(
        @Query("type") type: String,
        @Query("value") value: String
    ): Maybe<RecoverPasswordResponse>

    @GET("v1/user/password/recover/check")
    fun checkPasswordRecover(@Query("type") type: String, @Query("code") code: String): Completable

    @POST("v1/user/password/recover")
    fun recoverPassword(@Body body: RecoverPasswordBody): Single<AuthResponse>

    @GET("v1/user/{id}/password/check")
    fun checkPassword(@Path("id") id: Int, @Query("password") password: String): Completable

    @GET("v1/user-favorites")
    fun getFavoritesList(@QueryMap map: Map<String, Any>): Maybe<ApiNewResponse<List<FavoriteModel>>>

    @GET("v1/user-favorites")
    fun getEventFavoritesList(@QueryMap map: Map<String, Any>): Maybe<ApiNewResponse<List<EventFavoriteModel>>>

    @GET("v1/user-favorites")
    fun getUsersFavoritesList(@QueryMap map: Map<String, Any>): Maybe<ApiNewResponse<List<UsersFavoriteModel>>>

    @GET("v1/organization-member")
    fun getOrganizationMembers(@QueryMap map: Map<String, Any>): Maybe<ApiNewResponse<List<OrganizationMemberModel>>>

    @GET("v1/organization-member")
    fun getOrganizationMembersWithoutPagination(@QueryMap map: Map<String, Any>): Maybe<ApiNewResponse<List<OrganizationMemberModel>>>

    @GET("v1/user")
    fun getUsers(@QueryMap map: Map<String, Any>): Maybe<ApiNewResponse<List<UserDetail>?>>

    @PATCH("v1/user/{id}/unblock")
    fun unblockUser(@Path("id") id: Int): Completable

    @PATCH("v1/user/{id}/block")
    fun blockUser(@Path("id") id: Int): Completable

    @GET("v1/event-form")
    fun getEventForm(@QueryMap map: Map<String, Any>): Single<ApiNewResponse<List<EventFormModel>>>

    @GET("v1/event-form-result")
    fun getEventFormResult(@QueryMap map: Map<String, Any>): Single<ApiNewResponse<List<EventFormResultModel>>>

    @GET("v1/event-form-result/{id}/draft")
    fun getEventFormResultDraft(
        @Path("id") id: Int,
        @QueryMap map: Map<String, Any>
    ): Maybe<EventFormResultDraftModel>

    @POST("v1/event-form-result")
    fun eventRegister(@Body body: RequestBody): Single<ApiNewResponse<List<EventFormResultModel>>>

    //+
    @GET("v1/event-form-result/prefilled/{id}")
    fun getPrefilledEventFormResult(@Path("id") id: String): Single<EventRegisterProfilePrefilledData>

    //+
    @POST("v1/event-form-result")
    fun sendEventFormResultForRegister(@Body body: RequestBody): Single<EventFormResultModel>

    @POST("v1/event-user-registration/{id}/register")
    fun registerToEvent(@Path("id") id: Int, @Body body: RegisterToEventBody): Completable

    @PATCH("v1/event-user-registration/{id}/cancel")
    fun cancelRegisterToEvent(@Path("id") id: Int): Completable

    @POST("v1/user-calendar")
    fun addEventToCalendar(@Body body: EventCalendarBody): Completable

    @POST("v1/user-calendar")
    fun addEventToCalendarWithResult(@Body body: EventCalendarBody): Single<EventCalendarModel>

    @DELETE("v1/user-calendar/{user}/all")
    fun deleteAllCalendarEvents(
        @Path("user") id: Int,
        @Query("entityType") entityType: String
    ): Completable

    @DELETE("v1/user-calendar/{id}")
    fun deleteCalendarEvent(@Path("id") id: String): Completable

    @GET("v1/user-calendar")
    fun getUserCalendarEvent(
        @Query("user") userId: Int,
        @Query("entityType") entityType: String
    ): Maybe<ApiNewResponse<List<EventCalendarModel>>>

    @GET("v1/event-activity")
    fun getEventActivities(
        @Query("event") eventId: Int, @Query("binds") binds: String,
        @Query("sortField") sortField: String?, @Query("sortType") sortType: String?
    ): Maybe<ApiNewResponse<List<EventActivityModel>>>

    @GET("v1/event-activity/{id}")
    fun getEventActivity(
        @Path("id") activityId: String,
        @Query("binds") binds: String
    ): Single<EventActivityModel>

    //+
    @GET("v1/user-notification")
    fun getNotifications(@QueryMap map: Map<String, Any>): Maybe<NotificationsResponse<NotificationModel>>


    @GET("v1/user-notification/{id}")
    fun getNotificationDetail(
        @Path("id") notificationId: String,
        @Query("loadModel") loadModel: Boolean
    ): Single<NotificationModel>

    @PATCH("v1/user-notification/{id}/acknowledge")
    fun markAsRead(@Path("id") notificationId: String): Completable

    //+
    @PATCH("v1/user-notification/{id}/mark-as-read")
    fun markAllNotificationsAsRead(
        @Path("id") userId: String,
    ): Maybe<UnacceptedInviteNotification>

    @PATCH("v1/user-notification/{id}/mark-as-read")
    fun markAllTypeNotificationsAsRead(
        @Path("id") userId: String,
        @Query("type") type: String
    ): Maybe<UnacceptedInviteNotification>

    @POST("v1/user-notification/register-fcm")
    fun registerFcmToken(@Body body: FcmTokenBody): Completable

    @DELETE("v1/user-notification/unregister-fcm")
    fun unregisterFcmToken(@Body body: FcmTokenBody): Completable

    @GET("v1/user-external-invite/assistance/{id}")
    fun getInviteAssistanceDetail(@Path("id") assistanceId: String): Single<InviteDetail>

    @PATCH("v1/organization-member/{id}/approve")
    fun approveOrgMember(@Path("id") orgMemberId: String, @Body body: ApproveBody): Completable

    @PATCH("v1/organization-member/{id}/decline")
    fun declineOrgMember(@Path("id") orgMemberId: String, @Body body: DeclineBody): Completable

    @PATCH("v1/user-external-invite/pgrf/{id}/accept")
    fun approvePgrf(@Path("id") pgrfId: String): Completable

    @PATCH("v1/user-external-invite/pgrf/{id}/decline")
    fun declinePgrf(@Path("id") pgrfId: String): Completable

    @PATCH("v1/user-external-invite/assistance/{id}/accept")
    fun approveAssistance(@Path("id") assistanceId: String): Completable

    @PATCH("v1/user-external-invite/assistance/{id}/decline")
    fun declineAssistance(@Path("id") assistanceId: String): Completable

    @PATCH("v1/event-member/{id}/cancel")
    fun cancelEvMember(@Path("id") evMemberId: String, @Body body: CancelBody): Completable

    @PATCH("v1/event-member/{id}/approve")
    fun approveEventMember(@Path("id") memberId: String): Completable

    @PATCH("v1/event-member/{id}/decline")
    fun declineEventMember(@Path("id") memberId: String): Completable

    @PATCH("v1/user-external-invite/pgrf/{id}/rebase")
    fun rebaseInvite(@Path("id") id: Int, @Body body: RebaseInviteBody): Completable

    @GET("v1/chat-room")
    fun getChats(@QueryMap map: Map<String, Any>): Maybe<ApiNewResponse<List<ChatModel>>>

    @GET("v1/chat-room")
    fun getChatsCount(@QueryMap map: Map<String, Any>): Single<ApiNewResponse<List<ChatModel>>>

    @GET("v1/chat-room/{id}")
    fun getChatById(@Path("id") chatId: String, @QueryMap map: Map<String, Any>): Single<ChatModel>

    @GET("v1/event-tag")
    fun getTags(@QueryMap map: Map<String, Any>): Maybe<ApiNewResponse<List<EventTagModel>>>

    @POST("v1/chat-room")
    fun createChat(@Body body: CreateChatBody): Single<CreatedChatModel>

    @GET("v1/chat-ban")
    fun bannedList(@QueryMap map: Map<String, Any>): Maybe<ApiNewResponse<List<BannedUsersModel>>>

    @POST("v1/chat-ban")
    fun chatBan(@Body body: CreateChatBody): Single<BannedUsersModel>

    @DELETE("v1/chat-ban/{id}")
    fun deleteBan(@Path("id") id: Int): Completable

    @PATCH("v1/chat-room/{id}/accept")
    fun acceptChat(@Path("id") id: Int): Completable

    @DELETE("v1/user/{id}/email/confirm/reject")
    fun deleteConfirmEmail(@Path("id") id: Int, @Query("email") email: String): Completable

    @GET("v1/user/check-if-user-exist")
    fun checkEmailPhone(@Query("email") email: String?, @Query("phone") phone: String?): Completable

    @POST("v1/chat-message")
    fun sendChatMessage(@Body body: RequestBody): Single<MessageModel>

    //+
    @POST("v1/chat-message")
    fun sendChatMessageNew(@Body body: MessageBodyNew): Single<MessageModel>

    @GET("v1/chat-message")
    fun getChatMessages(@QueryMap map: Map<String, Any>): Single<ApiNewResponse<List<MessageModel>>>

    @PATCH("v1/chat-message/{id}/acknowledge")
    fun markMessageAsRead(@Path("id") id: Int): Completable

    @POST("v1/event-user-message")
    fun messageToEvent(@Body body: MessageToEventBody): Completable

    @GET("v1/search")
    fun searchGlobal(@QueryMap map: Map<String, Any>): Maybe<SearchDataNew>

    @GET("v1/organization/active-events")
    fun getOrganizationsWithActiveEvents(): Maybe<SearchResponseData<OrganizationNew>>

    @GET("v1/app-version/check")
    fun checkAppVersion(
        @Query("version") version: String, @Query("os") os: String
    ): Maybe<AppUpdateModel>


    //+
    @GET("v1/address/regions")
    fun getRegions(): Maybe<List<String>>

    @GET("v1/address/cities-events")
    fun getEventsTowns(@Query("query") region: String): Maybe<List<SearchTown>>

    @GET("v1/address/cities-users")
    fun getUsersTowns(@Query("query") region: String): Maybe<List<SearchTown>>

    @GET("v1/address/cities-orgs")
    fun getOrganizationsTowns(@Query("query") region: String): Maybe<List<SearchTown>>

    @GET("v1/address/settlements")
    fun getSettlements(@Query("query") region: String): Maybe<SearchResponseData<String>>

    //+
    @GET("v1/event-agreement/{id}")
    fun checkRegistrationAgreement(@Path("id") id: Int): Single<RegistrationAgreementStatus>

    @PATCH("v1/event-agreement/{id}")
    fun acceptRegistrationAgreement(@Path("id") id: Int): Single<RegistrationAgreementStatus>

    //+
    @GET("v1/support")
    fun getSupportData(): Maybe<List<SupportData>>

    @POST("v1/form/feedback-send")
    fun sendSupportData(@Body body: RequestBody): Completable

    @GET("v1/support/search")
    fun searchSupportQuestion(@Query("search") search: String) : Maybe<List<SupportData>>

    //+
    @POST("v1/user/social/bind")
    fun bindSocialAccountWithToken(
        @Body body: BindSocialAccountBody,
        @Header("Authorization") token: String?
    ): Completable

    @POST("v1/user/social/bind")
    fun bindSocialAccount(@Body body: BindSocialAccountBody): Maybe<SnBindDataModel>

    @HTTP(method = "DELETE", path = "v1/user/social/disconnect", hasBody = true)
    fun unBindSocialAccount(@Body map : Map<String, String>): Completable
}