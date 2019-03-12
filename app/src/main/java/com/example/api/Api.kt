package com.example.api

import com.example.data.models.*
import com.example.data.models.user.User
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

interface Api {

    @FormUrlEncoded
    @POST("/v1/user/auth/vk")
    fun authVk(@Field("token") token: String, @Field("email") email: String?): Single<ApiResponse<AuthResponse>>

    @FormUrlEncoded
    @POST("/v1/user/auth/ok")
    fun authOk(@Field("token") token: String): Single<ApiResponse<AuthResponse>>

    @FormUrlEncoded
    @POST("/v1/user/auth/fb")
    fun authFb(@Field("token") token: String): Single<ApiResponse<AuthResponse>>

    @FormUrlEncoded
    @POST("/v1/user/auth")
    fun authEmail(@Field("user_email") email: String, @Field("user_pwd") password: String): Single<ApiResponse<AuthResponse>>

    @FormUrlEncoded
    @POST("/v1/user/register")
    fun registerEmail(@Field("user_email") email: String, @Field("user_pwd") password: String, @Field("user_name") name: String, @Field("user_last_name") lastName: String): Single<ApiResponse<AuthResponse>>

    @FormUrlEncoded
    @POST("/v1/user/register/confirm")
    fun registerEmailConfirm(@Field("user_email") email: String, @Field("confirm_code") code: String): Single<ApiResponse<AuthResponse>>

    @GET("/v1/user/info/short")
    fun getUserShort(): Maybe<ApiResponse<User>>

    @GET("/v1/user/info")
    fun getUserFull(): Maybe<ApiResponse<User>>

    @GET("/v1/user/notifications/last")
    fun getLastNotification(): Single<ApiResponse<List<Notification>>>

    @FormUrlEncoded
    @POST("/v1/user/notifications")
    fun getUserNotifications(@Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<Notification>>>

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
    @POST("/v1/user/chat/search")
    fun chatSearch(@FieldMap searchMap: Map<String, @JvmSuppressWildcards Any>, @Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<UserChat>>>

    @FormUrlEncoded
    @POST("/v1/user/chat/{chat}/message")
    fun chatLastMessage(@Path("chat") chatId: String, @Field("message") message: String, @Field("message_id") messageId: String): Completable

    @POST("/v1/user/chat/start/{user}")
    fun startChat(@Path("user") userId: Int): Single<ApiResponse<ChatStartResponse>>

    @Multipart
    @POST("/v1/user/chat/{chat}/upload")
    fun uploadChatImage(@Path("chat") chatId: String, @Part image: MultipartBody.Part): Single<ApiResponseUpload<UploadImage>>

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
    fun getEventList(@Field("name") name: String?, @Field("date_start") dateStart: String?, @Field("date_end") dateEnd: String?, @Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<Event>>>

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
    fun speakerAddToFavorite(@Path("id") speakerId: Int): Completable

    @POST("/v1/speakers/{id}/unfavorite")
    fun speakerRemoveFromFavorite(@Path("id") speakerId: Int): Completable

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
}