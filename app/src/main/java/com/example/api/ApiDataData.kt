package com.example.api

import com.example.data.DataDataRequestBody
import com.example.data.models.*
import com.example.data.models.user.User
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiDataData {

    @Headers("Authorization: Token 19699841dd28be8acd2a1125c576c201b16e1143")
    @POST("suggestions/api/4_1/rs/suggest/address")
    fun suggestCity(@Body body:DataDataRequestBody): Single<DataDataResponse>

}