package com.example.api

import com.example.data.models.*
import com.example.data.models.user.User
import com.example.data.models.user.UserResp
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface Api {

    @FormUrlEncoded
    @POST("/v1/user/auth/{sn}")
    fun authSocialNetwork(
            @Path("sn") sn: String,
            @Field("token") token: String,
            @Field("email") email: String?,
            @Field("user_name") firstName: String? = null,
            @Field("user_last_name") lastName: String? = null,
            @Field("user_pwd") password: String? = null
    ): Single<ApiResponse<AuthSNResponse>>

    @FormUrlEncoded
    @POST("/v1/user/auth/{sn}/set_email")
    fun setEmailSocialNetwork(@Path("sn") sn: String, @Field("email") email: String, @Field("token") token: String): Single<ApiResponse<AuthResponse>>

    @FormUrlEncoded
    @POST("/v1/user/auth/social/confirm_email")
    fun confirmEmailSocialNetwork(@Field("id") id: String, @Field("code") code: String): Single<ApiResponse<AuthResponse>>

    /*@FormUrlEncoded
    @POST("/v1/user/auth")
    fun authEmailOrPhone(@Field("username") email: String, @Field("user_pwd") password: String): Single<ApiResponse<AuthResponse>>*/

    @FormUrlEncoded
    @POST("/v1/user/auth/status")
    fun registerStatus(@Field("user_email") email: String?, @Field("social_provider") snType: String?, @Field("social_id") snId: String?): Single<ApiResponse<RegisterStatus>>

    /*@FormUrlEncoded
    @POST("/v1/user/register")
    fun registerEmail(
            @Field("user_email") email: String,
            @Field("user_pwd") password: String,
            @Field("user_name") name: String,
            @Field("user_last_name") lastName: String,
            @Field("user_middle_name") middleName: String?,
            @Field("user_phone") phone: String?
    ): Single<ApiResponse<AuthResponse>>*/

    @FormUrlEncoded
    @POST("/v1/user/register/confirm")
    fun registerEmailConfirm(@Field("user_email") email: String, @Field("confirm_code") code: String,
    @Field("user_name") name: String, @Field("user_last_name") lastName: String,
    @Field("user_middle_name") middleName: String?, @Field("user_phone") phone: String?,
    @Field("user_email_new") newEmail: String? = null,
    @Field("user_password") password: String? = null): Single<ApiResponse<AuthResponse>>

    @FormUrlEncoded
    @POST("/v1/user/register/confirm/get")
    fun registerData(@Field("user_email") email: String, @Field("confirm_code") code: String): Single<ApiResponse<UserResp>>

    @FormUrlEncoded
    @POST("/v1/user/update/change_email_confirm")
    fun changeEmailConfirm(@Field("user_email") email: String, @Field("confirm_code") code: String): Single<ApiResponse<AuthResponse>>

    /*@FormUrlEncoded
    @POST("/v1/user/register_resend")
    fun registerEmailResend(@Field("user_email") email: String): Single<ApiResponse<AuthResponse>>

    @FormUrlEncoded
    @POST("/v1/user/auth/social/resend_confirm")
    fun registerSnResend(@Field("email") email: String, @Field("token") token: String): Single<ApiResponse<AuthResponse>>*/

    @GET("/v1/user/info/short")
    fun getUserShort(): Maybe<ApiResponse<UserShort>>

    @GET("/v1/user/info")
    fun getUserFull(): Maybe<ApiResponse<User>>

    @FormUrlEncoded
    @POST("/v1/user/check_pwd")
    fun checkPassword(@Field("user_password") password: String): Completable

    @FormUrlEncoded
    @POST("/v1/user/phone/sms")
    fun sendStatusPhoneConfirmSms(@Field("user_password") password: String): Completable

    @FormUrlEncoded
    @POST("/v1/user/phone/confirm")
    fun sendStatusPhoneConfirmCode(@Field("code") code: String): Completable

    @GET("/v1/users/{id}")
    fun getUserById(@Path("id") id: String): Maybe<ApiResponse<User>>

    @FormUrlEncoded
    @POST("/v1/users")
    fun getUsersList(@Field("limit") limit: Int, @Field("start") offset: Int, @FieldMap filter: Map<String, @JvmSuppressWildcards Any>?): Maybe<ApiResponse<List<User>>>

    @GET("/v1/user/notifications/last")
    fun getLastNotification(): Single<ApiResponse<List<Notification>>>

    @FormUrlEncoded
    @POST("/v1/user/notifications")
    fun getUserNotifications(@Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<RemoteNotification>>>

    @GET("/v1/user/notifications/{id}")
    fun getUserNotification(@Path("id") id: Int): Maybe<ApiResponse<RemoteNotification>>

    @FormUrlEncoded
    @POST("/v1/user/notifications/mark_as_read")
    fun markNotificationsAsRead(@Field("id[]") ids: List<Int>): Maybe<ApiResponse<UnreadCountResponse>>

    @FormUrlEncoded
    @POST("/v1/user/notifications/register")
    fun notificationsRegister(@Field("token") token: String): Completable

    @FormUrlEncoded
    @POST("/v1/user/notifications/unregister")
    fun notificationsUnregister(@Field("token") token: String): Completable

    @POST("/v1/user/notifications/{id}/answer/accept")
    fun notificationsInviteAccept(@Path("id") id: Int): Maybe<ApiResponse<UnreadCountResponse>>

    @POST("/v1/user/notifications/{id}/answer/decline")
    fun notificationsInviteDecline(@Path("id") id: Int): Maybe<ApiResponse<UnreadCountResponse>>

    @FormUrlEncoded
    @POST("/v1/user/chat/list")
    fun chatList(@Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<ChatListResponse>>

    @FormUrlEncoded
    @POST("/v1/user/chat/list")
    fun chatListInvites(@Field("limit") limit: Int, @Field("start") offset: Int, @Field("view_invites") showInvites: Int = 1): Maybe<ApiResponse<List<UserChat>>>

    @FormUrlEncoded
    @POST("/v1/user/chat/list")
    fun chatListBans(@Field("limit") limit: Int, @Field("start") offset: Int, @Field("view_banned") showInvites: Int = 1): Maybe<ApiResponse<List<UserChat>>>

    @FormUrlEncoded
    @POST("/v1/user/chat/search")
    fun chatSearch(@FieldMap searchMap: Map<String, @JvmSuppressWildcards Any>, @Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<User>>>

    @POST("/v1/user/chat/start/{user}")
    fun chatStart(@Path("user") userId: String): Single<ApiResponse<ChatStartResponse>>

    @Multipart
    @POST("/v1/user/chat/{chat}/upload")
    fun uploadChatImage(@Path("chat") chatId: String, @Part image: MultipartBody.Part): Single<ApiResponseUpload<UploadImage>>

    @POST("/v1/user/chat/{chat}/accept")
    fun chatAccept(@Path("chat") chatId: String): Completable

    @POST("/v1/user/chat/{chat}/bann/on")
    fun chatBan(@Path("chat") chatId: String): Completable

    @POST("/v1/user/chat/{chat}/bann/off")
    fun chatUnban(@Path("chat") chatId: String): Completable

    @GET("/v1/user/chat/count/invites")
    fun getChatInvitesCount(): Single<ApiResponse<ChatInvitesCount>>

    @Multipart
    @POST("/v1/user/update/recomend_file")
    fun uploadDocument(@Part image: MultipartBody.Part): Single<ApiResponse<User>>

    @Multipart
    @POST("/v1/user/update/avatar")
    fun uploadAvatar(@Part image: MultipartBody.Part?): Single<ApiResponse<User>>

    @Headers("Content-Type: application/json")
    @POST("/v1/user/update")
    fun updateUser(@Body map: Map<String, @JvmSuppressWildcards Any?>): Single<ApiResponse<User>>

    @POST("/v1/user/event_calendar")
    fun eventCalendar(): Maybe<ApiResponse<List<UserEventCalendar>>>

    @FormUrlEncoded
    @POST("/v1/user/geo")
    fun setUserAtEvent(@Field("event[]") events: List<Int>, @Field("at_event[]") atEvent: List<Boolean>, @Field("lat") lat: Double, @Field("lon") lon: Double): Completable

    @POST("v1/user/deactivate")
    fun deleteProfile(): Completable

    @FormUrlEncoded
    @POST("/v1/events")
    fun getEventList(@Field("limit") limit: Int, @Field("start") offset: Int, @FieldMap filter: Map<String, @JvmSuppressWildcards Any>?): Maybe<ApiResponse<List<Event>>>

    @FormUrlEncoded
    @POST("/v1/events/recommendations")
    fun getEventRecommendations(@Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<Event>>>

    @FormUrlEncoded
    @POST("/v1/users/search")
    fun userSearch(@Field("user_fio") name: String, /*@Field("user_email") email: String,*/@Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<User>>>

    @GET("/v1/user/chat/{chat}")
    fun getChat(@Path("chat") chatId: String): Single<ApiResponse<UserChat>>

    @FormUrlEncoded
    @POST("/v1/user/recovery_password")
    fun sendEmailRecovery(@Field("user_email") email: String): Single<ApiResponse<AuthResponse>>

    @FormUrlEncoded
    @POST("/v1/user/recovery_password/check")
    fun checkRecoveryCode(@Field("user_email") email: String, @Field("confirm_code") confirm: String): Single<ApiResponse<AuthResponse>>

    @FormUrlEncoded
    @POST("/v1/user/recovery_password/set_pwd")
    fun setPassword(@Field("user_email") email: String, @Field("confirm_code") confirm: String, @Field("user_pwd") password: String): Single<ApiResponse<AuthResponse>>

    @POST("/v1/users/{id}/favorite")
    fun userAddToFavorite(@Path("id") uid: String): Completable

    @POST("/v1/users/{id}/unfavorite")
    fun userRemoveFromFavorite(@Path("id") uid: String): Completable

    @FormUrlEncoded
    @POST("/v1/organisations")
    fun getOrganizations(@Field("limit") limit: Int, @Field("start") offset: Int, @FieldMap filter: Map<String, @JvmSuppressWildcards Any>?): Maybe<ApiResponse<List<Organization>>>

    @GET("/v1/organisations/{id}")
    fun getOrganizationById(@Path("id") id: String): Single<ApiResponse<OrganizationData>>

    @POST("/v1/organisations/{organizationId}/subscribe")
    fun organizationSubscribe(@Path("organizationId") orgId: String): Completable

    @POST("/v1/organisations/{organizationId}/unsubscribe")
    fun organizationUnsubscribe(@Path("organizationId") orgId: String): Completable

    @FormUrlEncoded
    @POST("/v1/organisations/{organizationId}/members")
    fun organizationMembers(@Path("organizationId") orgId: String, @Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<OrganizationMember>>>

    @GET("/v1/events/{eventId}/register/fields")
    fun getEventRegisterField(@Path("eventId") eventId: String): Single<ApiResponse<EventRegisterForm>>

    @POST("/v1/events/{eventId}/register/save")
    fun eventRegister(@Path("eventId") eventId: String, @Body body: RequestBody): Single<ApiResponse<EventRegisterResponse>>

    @GET("/v1/events/{eventId}/register/check")
    fun eventRegisterCheck(@Path("eventId") eventId: String): Single<ApiResponse<EventRegisterCheckFields>>

    @POST("/v1/events/{eventId}/register/cancel")
    fun eventRegisterCancel(@Path("eventId") eventId: String): Completable

    @GET("/v1/events/{eventId}/register")
    fun getEventRegister(@Path("eventId") eventId: String): Single<ApiResponse<EventRegisterResponse>>

    @GET("/v1/events/{eventId}/rating/fields")
    fun getEventRatingForm(@Path("eventId") eventId: String): Single<ApiResponse<List<EventRegisterField>>>

    @POST("/v1/events/{eventId}/rating/set")
    fun setEventRating(@Path("eventId") eventId: String, @Body body: RequestBody): Completable

    @GET("/v1/events/{eventId}/rating/get")
    fun getEventRating(@Path("eventId") eventId: String): Single<ApiResponse<EventInfo>>

    @GET("/v1/events/{eventId}")
    fun getEventInfo(@Path("eventId") eventId: String): Maybe<ApiResponse<EventInfo>>

    @POST("/v1/events/{eventId}/activity")
    fun getEventActivity(@Path("eventId") eventId: String): Maybe<ApiResponse<EventActivity>>

    @GET("/v1/events/{eventId}/activity/{subEventId}")
    fun getSubEvent(@Path("eventId") eventId: String, @Path("subEventId") subEventId: String): Single<ApiResponse<SubeventInfo>>

    @POST("v1/user/set_default_event/{eventId}")
    fun setDefaultEvent(@Path("eventId") eventId: String): Completable

    @FormUrlEncoded
    @POST("/v1/events/{eventId}/activity/{subEventId}/contacts")
    fun getSubEventUsers(@Path("eventId") eventId: String, @Path("subEventId") subEventId: String, @Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<User>>>

    @POST("/v1/events/{eventId}/activity/{subEventId}/add2calendar")
    fun addSubEventToCalendar(@Path("eventId") eventId: String, @Path("subEventId") subEventId: String): Completable

    @POST("/v1/events/{eventId}/activity/{subEventId}/remove4calendar")
    fun removeSubEventFromCalendar(@Path("eventId") eventId: String, @Path("subEventId") subEventId: String): Completable

    @POST("/v1/events/{eventId}/partners")
    fun getPartnerListByEvent(@Path("eventId") eventId: String): Single<ApiResponse<List<Partner>>>

    @GET("/v1/events/{eventId}/partners/{partnerId}")
    fun getPartnerById(@Path("eventId") eventId: String, @Path("partnerId") partnerId: String): Single<ApiResponse<Partner>>

    @GET("/v1/events/groups")
    fun getCategoriesList(): Single<ApiResponse<List<EventGroup>>>

    @FormUrlEncoded
    @POST("/v1/events/{eventId}/speakers")
    fun getEventSpeakers(@Path("eventId") eventId: String, @Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<Speaker>>>

    @GET("/v1/events/qr/{code}")
    fun getEventByCode(@Path("code") code: String): Single<ApiResponse<QrEvent>>

    @GET("/v1/events/{event}/pages/{page}")
    fun getEventPage(@Path("event") event: String, @Path("page") page: String): Single<ApiResponse<Page>>

    @POST("/v1/events/{event}/subscribe")
    fun subscribeToEvent(@Path("event") event: String): Completable

    @POST("/v1/events/{event}/unsubscribe")
    fun unsubscribeFromEvent(@Path("event") event: String): Completable

    @FormUrlEncoded
    @POST("/v1/events/favorites")
    fun getFavoriteEvents(@Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<Event>>>

    @FormUrlEncoded
    @POST("/v1/events/{event}/messages/add")
    fun sendMessageToOrganization(@Path("event") event: String, @Field("message") message: String): Completable

    @POST("/v1/events/{event}/activity/{activity}/subscribe")
    fun subscrbeToSubevent(@Path("event") event: String, @Path("activity") activity: String): Completable

    @POST("/v1/events/{event}/activity/{activity}/unsubscribe")
    fun unsubscribeFromSubEvent(@Path("event") event: String, @Path("activity") activity: String): Completable

    @POST("/v1/common/interests")
    fun getInterestsList(): Maybe<ApiResponse<List<Interest>>>

    @GET("/v1/common/page_agreement")
    fun getUserAgreement(): Maybe<ApiResponse<Agreement>>

    @POST("/v1/common/formats")
    fun getEventFormats(): Maybe<ApiResponse<List<EventFormat>>>
}