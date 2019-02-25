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

    @GET("/v1/user/info")
    fun getUser(): Maybe<ApiResponse<User>>

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
    @POST("http://api.ha-slsp.ru/v1/user/chat/search")
    fun chatSearch(@FieldMap searchMap: Map<String, @JvmSuppressWildcards Any>, @Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<UserChat>>>

    @FormUrlEncoded
    @POST("/v1/user/chat/{chat}/message")
    fun chatLastMessage(@Path("chat") chatId: String, @Field("message") message: String): Completable

    @POST("/v1/user/chat/start/{user}")
    fun startChat(@Path("user") userId: Int): Single<ApiResponse<ChatStartResponse>>

    @Multipart
    @POST("/v1/user/chat/{chat}/upload")
    fun uploadChatImage(@Path("chat") chatId: String, @Part image: MultipartBody.Part): Single<ApiResponseUpload<UploadImage>>

    @POST("/v1/user/update")
    fun updateUser(@Body user: User): Single<ApiResponse<User>>

    @FormUrlEncoded
    @POST("/v1/events")
    fun getEventList(@Field("name") name: String?, @Field("date_start") dateStart: String?, @Field("date_end") dateEnd: String?, @Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<Event>>>


    @FormUrlEncoded
    @POST("/v1/users/search")
    fun userSearch(@Field("user_fio") name: String, /*@Field("user_email") email: String,*/@Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<User>>>
}