package com.example.di

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.api.AuthInterceptor
import com.example.api.Api
import com.example.data.AppData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import com.example.extensions.isConnectedToNetwork
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class RetrofitModule {

    companion object {
        const val HEADER_CACHE_CONTROL = "Cache-Control"
    }

    @Provides
    @Singleton
    fun provideApi(converterFactory: Converter.Factory, authInterceptor: AuthInterceptor, context: Context): Api {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .cache(Cache(File(context.cacheDir, "http-cache"), 10 * 1024 * 1024))

        clientBuilder.addInterceptor(authInterceptor)

        if (BuildConfig.DEBUG) {
            val logInterceptor = HttpLoggingInterceptor { message ->
                Log.e("REQUEST INFO: ", message)
            }
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(logInterceptor)
        }

        clientBuilder.addNetworkInterceptor {
            val response = it.proceed(it.request())
            val cacheControl: CacheControl = if (context.isConnectedToNetwork()) {
                CacheControl.Builder().maxAge(0, TimeUnit.SECONDS).build()
            } else {
                CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()
            }

            return@addNetworkInterceptor response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader(HEADER_CACHE_CONTROL)
                .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                .build()
        }

        clientBuilder.addInterceptor {
            var request = it.request()

            if (!context.isConnectedToNetwork()) {
                val cacheControl = CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()

                request = request.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build()
            }

            return@addInterceptor it.proceed(request)
        }

        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(converterFactory)
            .client(clientBuilder.build()).baseUrl(BuildConfig.NEW_API_URL).build().create(Api::class.java)
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
        return GsonBuilder().serializeNulls().create()
    }
}
