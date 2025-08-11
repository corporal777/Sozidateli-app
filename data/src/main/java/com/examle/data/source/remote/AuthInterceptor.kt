package com.examle.data.source.remote

import com.examle.data.AppData
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(private val appData: AppData) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
        val token = appData.token ?: appData.tempToken ?: ""
        if (token.isNotEmpty())
            authenticatedRequest.header("Authorization", "Token $token")
        return chain.proceed(authenticatedRequest.build())
    }
}