package com.example.di

import android.content.Context
import com.example.BuildConfig
import com.example.api.Api
import com.example.api.ApiDataData
import com.example.api.AuthInterceptor
import com.example.data.AppData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import com.facebook.FacebookSdk.getCacheDir
import okhttp3.Cache
import java.io.File
import isConnectedToNetwork
import io.fabric.sdk.android.services.network.HttpRequest.HEADER_CACHE_CONTROL
import okhttp3.CacheControl
import io.fabric.sdk.android.services.network.HttpRequest.HEADER_CACHE_CONTROL


@Module
class DataDataRetrofitModule {

    @Provides
    @Singleton
    fun provideApi(): ApiDataData {
        val retrofit = retrofit2.Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .baseUrl(BuildConfig.DATA_DATA_API_URL)

        val clientBuilder = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)

        if (BuildConfig.DEBUG) {
            val logInterceptor = HttpLoggingInterceptor { message -> Timber.tag("DATA_DATA").d(message) }
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(logInterceptor)
        }

        retrofit.client(clientBuilder.build())

       return retrofit.build().create(ApiDataData::class.java)
    }
}
