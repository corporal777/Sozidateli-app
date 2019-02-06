package com.example.api

import com.example.data.models.DefaultResponse
import com.example.data.models.user.User
import io.reactivex.Single
import retrofit2.http.GET

interface Api {


    @GET("/v1/user/info")
    fun getUser():Single<DefaultResponse<User>>
}