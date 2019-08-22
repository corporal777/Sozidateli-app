package com.example.api

import com.example.data.DaDataRequestBody
import com.example.data.models.DaDataResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiDataData {

    @Headers("Authorization: Token 19699841dd28be8acd2a1125c576c201b16e1143")
    @POST("suggestions/api/4_1/rs/suggest/address")
    fun suggestCity(@Body body: DaDataRequestBody): Single<DaDataResponse>
}