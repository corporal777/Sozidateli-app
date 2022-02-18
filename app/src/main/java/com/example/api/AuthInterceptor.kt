package com.example.api

import android.util.Log
import com.example.data.AppData
import okhttp3.Interceptor
import okhttp3.Response
import ru.ok.android.sdk.LOG_TAG
import java.io.IOException
import kotlin.jvm.Throws


class AuthInterceptor(private val appData: AppData) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
        appData.token?.let { authenticatedRequest.header("access-token", it) }
        return chain.proceed(authenticatedRequest.build())
    }
}
