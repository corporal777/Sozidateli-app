package com.example.api

import com.example.data.models.ApiResponse
import com.example.data.models.AuthResponse
import com.example.data.models.Notification
import com.example.data.models.user.User
import io.reactivex.Maybe
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

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

    @GET("/v1/user/info")
    fun getUser(): Single<ApiResponse<User>>

    @GET("/v1/user/notifications/last")
    fun getLastNotification(): Single<ApiResponse<List<Notification>>>

    @FormUrlEncoded
    @POST("/v1/user/notifications")
    fun getUserNotifications(@Field("limit") limit: Int, @Field("start") offset: Int): Maybe<ApiResponse<List<Notification>>>
}