package com.example.util

import com.example.data.models.ApiError
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import java.io.IOException

class ApiErrorParser {

    companion object {
        fun parse(throwable: Throwable?): ApiError? {
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
                    try {
                        return GsonBuilder().setLenient()
                                .create()
                                .fromJson(body, ApiError::class.java)
                    } catch (e: JsonSyntaxException) {
                        // ignore
                    }
                }
            }

            return null
        }
    }
}