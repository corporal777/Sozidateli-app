package com.examle.data.di

import com.examle.data.BuildConfig
import com.examle.data.source.remote.ApiDataData
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataDataRetrofitModule {

    @Provides
    @Singleton
    fun provideApi(): ApiDataData {
        val retrofit = retrofit2.Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .baseUrl("BuildConfig.DATA_DATA_API_URL")

        val clientBuilder = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)

        if (BuildConfig.DEBUG) {
            val logInterceptor = HttpLoggingInterceptor { message ->

            }
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(logInterceptor)
        }

        retrofit.client(clientBuilder.build())

        return retrofit.build().create(ApiDataData::class.java)
    }
}
