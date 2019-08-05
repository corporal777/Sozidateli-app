package com.example.api

import com.example.data.models.*
import com.example.data.models.user.User
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface Api {

    @FormUrlEncoded
    @POST("/v1/user/auth/{sn}")
    fun authSocialNetwork(@Path("sn") sn: String, @Field("token") token: String, @Field("email") email: String?): Single<ApiResponse<AuthSNResponse>>

    @FormUrlEncoded
    @POST("/v1/user/auth/{sn}/set_email")
    fun setEmailSocialNetwork(@Path("sn") sn: String, @Field("email") email: String, @Field("token") token: String): Single<ApiResponse<AuthResponse>>

    @FormUrlEncoded
    @POST("/v1/user/auth/{sn}/confirm_email")
    fun confirmEmailSocialNetwork(@Path("sn") sn: String, @Field("id") id: String, @Field("code") code: String): Single<ApiResponse<AuthResponse>>

    @FormUrlEncoded
    @POST("/v1/user/auth")
    fun authEmail(@Field("user_email") email: String, @Field("user_pwd") password: String): Single<ApiResponse<AuthResponse>>

    @FormUrlEncoded
    @POST("/v1/user/register")
    fun registerEmail(@Field("user_email") email: String, @Field("user_pwd") password: String, @Field("user_name") name: String, @Field("user_last_name") lastName: String): Single<ApiResponse<AuthResponse>>

    @FormUrlEncoded
    @POST("/v1/user/register/confirm")
    fun registerEmailConfirm(@Field("user_email") email: String, @Field("confirm_code") code: String): Single<ApiResponse<AuthResponse>>

    @FormUrlEncoded
    @POST("/v1/user/update/change_email_confirm")
    fun changeEmailConfirm(@Field("user_email") email: String, @Field("confirm_code") code: String): Single<ApiResponse<AuthResponse>>

    @GET("/v1/user/info/short")
    fun getUserShort(): Maybe<ApiResponse<User>>

    @GET("/v1/user/info")
    fun getUserFull(): Maybe<ApiResponse<User>>

    @GET("/v1/users/{id}")
    fun getUserById(@Path("id") id: String): Single<ApiResponse<User>>

    @GET("/v1/user/notifications/last")
    fun getLastNotification(): Single<ApiResponse<List<Notification>>>

    @FormUrlEncoded
    @POST("/v1/user/notifications")
    fun getUserNotifications(@Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<Notification>>>

    @FormUrlEncoded
    @POST("/v1/user/notifications/mark_as_read")
    fun markNotificationsAsRead(@Field("id[]") ids: List<Int>): Maybe<ApiResponse<MarkedResponse>>

    @FormUrlEncoded
    @POST("/v1/user/notifications/register")
    fun notificationsRegister(@Field("token") token: String): Completable

    @FormUrlEncoded
    @POST("/v1/user/notifications/unregister")
    fun notificationsUnregister(@Field("token") token: String): Completable

    @FormUrlEncoded
    @POST("/v1/user/favorites/speakers")
    fun getUserFavoriteSpeakers(@Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<Speaker>>>

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
    fun uploadAvatar(@Part image: MultipartBody.Part): Completable

    @Headers("Content-Type: application/json")
    @POST("/v1/user/update")
    fun updateUser(@Body map: Map<String, @JvmSuppressWildcards Any?>): Single<ApiResponse<User>>

    @FormUrlEncoded
    @POST("/v1/events")
    fun getEventList(@Field("limit") limit: Int, @Field("start") offset: Int,
                     @Field("name") name: String? = null, @Field("date_start") dateStart: String? = null,
                     @Field("date_end") dateEnd: String? = null, @Field("category[]") category: List<String>? = null,
                     @Field("organisation[]") organisation: List<String>? = null,
                     @Field("qr") qr: String? = null): Maybe<ApiResponse<List<Event>>>

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

    @FormUrlEncoded
    @POST("/v1/events/{eventId}/news")
    fun getEventNewsList(@Path("eventId") eventId: Int, @Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<News>>>

    @GET("/v1/events/{eventId}/news/{newsId}")
    fun getNewsById(@Path("eventId") eventId: Int, @Path("newsId") newsId: Int): Single<ApiResponse<News>>

    @FormUrlEncoded
    @POST("/v1/events/{eventId}/docs")
    fun getEventDocs(@Path("eventId") eventId: Int, @Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<Document>>>

    @POST("/v1/speakers/{id}/favorite")
    fun userAddToFavorite(@Path("id") uid: String): Completable

    @POST("/v1/speakers/{id}/unfavorite")
    fun userRemoveFromFavorite(@Path("id") uid: String): Completable

    @POST("/v1/organisations/{organizationId}/subscribe")
    fun organizationSubscribe(@Path("organizationId") orgId: Int): Completable

    @POST("/v1/organisations/{organizationId}/unsubscribe")
    fun organizationUnsubscribe(@Path("organizationId") orgId: Int): Completable

    @FormUrlEncoded
    @POST("/v1/user/subscribed/organisations")
    fun organizationSubscribeList(@Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<Organization>>>

    @POST("/v1/organisations/{organizationId}/favorite")
    fun organizationAddToFavorite(@Path("organizationId") orgId: Int): Completable

    @POST("/v1/organisations/{organizationId}/unfavorite")
    fun organizationRemoveFromFavorite(@Path("organizationId") orgId: Int): Completable

    @FormUrlEncoded
    @POST("/v1/user/favorites/organisations")
    fun organizationFavoriteList(@Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<Organization>>>

    @GET("/v1/events/{eventId}/register/fields")
    fun getEventRegisterField(@Path("eventId") eventId: Int): Single<ApiResponse<RegisterFieldResponse>>

    @Multipart
    @POST("/v1/events/{eventId}/register/save")
    fun eventRegister(@Path("eventId") eventId: Int, @PartMap map: Map<String, @JvmSuppressWildcards RequestBody?>, @Part data: List<MultipartBody.Part?>?): Completable

    @GET("/v1/events/{eventId}/register")
    fun getEventRegister(@Path("eventId") eventId: Int): Single<ApiResponse<EventRegisterResponse>>

    @GET("/v1/events/{eventId}")
    fun getEventInfo(@Path("eventId") eventId: Int): Maybe<ApiResponse<EventInfo>>

    @FormUrlEncoded
    @POST("/v1/events/{eventId}/activity")
    fun getEventActivity(@Path("eventId") eventId: Int, @Field("limit") limit: String = "all"): Maybe<ApiResponse<List<SubEvent>>>

    @FormUrlEncoded
    @POST("/v1/user/events/registrations")
    fun getEventRegisterList(@Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<EventRegisterResponse>>>

    @POST("v1/user/set_default_event/{eventId}")
    fun setDefaultEvent(@Path("eventId") eventId: Int): Completable

    @GET("/v1/events/{eventId}/activity/{subEventId}")
    fun getSubEvent(@Path("eventId") eventId: Int, @Path("subEventId") subEventId: Int): Single<ApiResponse<SubeventInfo>>

    @FormUrlEncoded
    @POST("/v1/events/{eventId}/activity/{subEventId}/contacts")
    fun getSubEventUsers(@Path("eventId") eventId: Int, @Path("subEventId") subEventId: Int, @Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<User>>>

    @POST("/v1/events/{eventId}/activity/{subEventId}/add2calendar")
    fun addSubEventToCalendar(@Path("eventId") eventId: Int, @Path("subEventId") subEventId: Int): Completable

    @POST("/v1/events/{eventId}/activity/{subEventId}/remove4calendar")
    fun removeSubEventFromCalendar(@Path("eventId") eventId: Int, @Path("subEventId") subEventId: Int): Completable

    @GET("/v1/events/{eventId}/map")
    fun getEventMapInfo(@Path("eventId") eventId: Int): Single<ApiResponse<MapInfo>>

    @POST("/v1/events/{eventId}/partners")
    fun getPartnerListByEvent(@Path("eventId") eventId: Int): Single<ApiResponse<List<Partner>>>

    @GET("/v1/partners/{partnerId}")
    fun getPartnerById(@Path("partnerId") partnerId: Int): Single<ApiResponse<Partner>>

    @POST("/v1/organisations")
    fun getOrganizationList(): Single<ApiResponse<List<Organization>>>

    @GET("/v1/events/categories")
    fun getCategoriesList(): Single<ApiResponse<List<Category>>>

    @FormUrlEncoded
    @POST("/v1/events/{eventId}/speakers")
    fun getEventSpeakers(@Path("eventId") eventId: Int, @Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<Speaker>>>

    @POST("/v1/users/interests")
    fun getItnrestsList(): Single<ApiResponse<List<Interest>>>
}