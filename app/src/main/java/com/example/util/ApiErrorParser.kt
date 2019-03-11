package com.example.util

import com.example.data.models.ApiResponse
import com.example.data.models.user.User
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException


class ApiErrorParser {

    companion object {

        private const val API_CONNECTION_ERROR = "No internet connection, please try again later"

        fun parse(throwable: Throwable?): ApiResponse<*>? {
            if (throwable == null) return null
            if (throwable is HttpException) {
                val responseBody = throwable.response().errorBody()
                var body: String? = null
                if (responseBody != null) {
                    try {
                        body = responseBody.string()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                if (body != null) {
                    return try {
                        val gson = GsonBuilder()
                                .setLenient()
                                .create()
                        gson.fromJson(body, ApiResponse::class.java)
                    } catch (e: JsonSyntaxException) {
                        return ApiResponse(null, Any(), null, null, 0, listOf(throwable.message()))
                    }
                }
            } else if (throwable is ConnectException) {
                return ApiResponse(null, User(), null, null, 0, listOf(API_CONNECTION_ERROR))
            }

            return null
        }
    }
}