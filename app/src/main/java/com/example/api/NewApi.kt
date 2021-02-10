package com.example.api

import androidx.room.Delete
import com.example.data.bodies.*
import com.example.data.models.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

@JvmSuppressWildcards
interface NewApi {

    @POST("v1/user/login")
    fun authEmailOrPhone(@Body body: AuthBody): Single<NewAuthResponse>

    @GET("v1/user/{id}")
    fun getUserShort(@Path("id") id: Int, @Query("binds") binds: List<String>?): Maybe<UserDetail>

    @POST("v1/user")
    fun registerEmail(@Body body: RegisterBody): Single<UserDetail>

    @GET("v1/user/email/confirm")
    fun registerEmailResend(@Query("email") email: String): Completable

    @PATCH("v1/user/{id}")
    fun updateProfile(@Path("id") id: Int, @Body map: Map<String, Any?>): Single<UserDetail>

    @POST("v1/user/{id}/email/confirm")
    fun confirmEmailCode(@Path("id") id: Int, @Body body: EmailCodeBody): Single<ConfirmEmail>

    @POST("v1/user/{id}/phone/confirm")
    fun confirmPhoneCode(@Path("id") id: Int, @Body body: PhoneCodeBody): Completable

    @GET("v1/user/{id}/phone/confirm")
    fun sendPhoneCode(@Path("id") id: Int, @Query("phone") phone: String): Completable

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
    fun changeUserImage(@Path("id") id: Int, @Part image: MultipartBody.Part?): Single<ImageModel>

    @DELETE("v1/user/{id}/image")
    fun deleteImage(@Path("id") id: Int): Completable

    @Multipart
    @POST("v1/user-recommendation-file")
    fun uploadRecommendedFile(@Part body: List<MultipartBody.Part?>): Single<ImageModel>

    @Multipart
    @PATCH("v1/user-recommendation-file/{id}")
    fun changeRecommendedFile(@Path("id") fileId : Int, @Part body: List<MultipartBody.Part?>): Single<ImageModel>

    @DELETE("v1/user-recommendation-file/{id}")
    fun deleteRecommendedFile(@Path("id") fileId : Int): Completable

    @GET("v1/user/{id}/password/check")
    fun checkIfPasswordValid(@Path("id") id : Int, @Query("password") password: String): Completable

    @PATCH("v1/user-work-experience/user/{id}")
    fun updateWorkExperience(@Path("id") id : Int, @Body body: WorkExperienceServerModel): Single<WorkExperienceServerModel>

    @GET("v1/interest")
    fun getInterestsList(@Query("limit") limit: Int, @Query("id") ids: List<Int>?): Single<InterestsModel>

    @GET("v1/education-level")
    fun getEducationLevel(): Single<EducationLevelModel>

    @GET("v1/speciality")
    fun getSpeciality(@Query("limit") limit: Int): Single<EducationLevelModel>

    @GET("v1/academic-degrees")
    fun getAcademicDegrees(): Single<EducationLevelModel>

    @PATCH("v1/user-education/user/{id}")
    fun updateUserEducation(@Path("id") id : Int, @Body body: EducationBodyModel): Single<EducationBodyModel>

    @PATCH("v1/user-academic-degree/user/{id}")
    fun updateUserAcademicDegree(@Path("id") id : Int, @Body body: AcademicDegreeBodyModel): Single<AcademicDegreeBodyModel>
}