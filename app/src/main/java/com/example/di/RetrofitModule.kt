package com.example.di

import com.example.BuildConfig
import com.example.api.Api
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

@Module
class RetrofitModule {

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): Api = retrofit.create(Api::class.java)

    @Provides
    @Singleton
    fun provideRetrofit(builder: Retrofit.Builder): Retrofit {
        return builder.baseUrl(BuildConfig.API_URL).build()
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(converterFactory: Converter.Factory, client: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(converterFactory)
                .client(client)
    }

    @Provides
    @Singleton
    fun provideHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)

        clientBuilder.addInterceptor(authInterceptor)

        if (BuildConfig.DEBUG) {
            val logInterceptor = HttpLoggingInterceptor { message -> Timber.tag("API_T").d(message) }
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(logInterceptor)
        }

        return clientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(appData: AppData): AuthInterceptor {
        return AuthInterceptor(appData)
    }

    @Provides
    @Singleton
    fun provideConverterFactory(gson: Gson): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }
}
