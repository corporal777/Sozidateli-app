package com.example.di

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.api.Api
import com.example.api.AuthInterceptor
import com.example.api.AuthInterceptorNew
import com.example.api.NewApi
import com.example.data.AppData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.shakebugs.shake.network.ShakeNetworkInterceptor
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import isConnectedToNetwork
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
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
    fun provideHttpClient(authInterceptor: AuthInterceptor, context: Context): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .cache(Cache(File(context.cacheDir, "http-cache"), 10 * 1024 * 1024))

        clientBuilder.addInterceptor(authInterceptor)

        if (BuildConfig.DEBUG) {
            val logInterceptor = HttpLoggingInterceptor { message ->
                Timber.tag("API_T").d(message)
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

        return clientBuilder.build()
    }

    // New
    @Provides
    @Singleton
    fun provideNewApi(converterFactory: Converter.Factory, authInterceptor: AuthInterceptorNew, context: Context): NewApi {
        val clientBuilder = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .cache(Cache(File(context.cacheDir, "http-cache"), 10 * 1024 * 1024))

        clientBuilder.addInterceptor(authInterceptor)

        if (!BuildConfig.DEBUG) {
            val logInterceptor = HttpLoggingInterceptor { message ->
                //Timber.tag("API_T").d(message)
                Log.e("REQUEST INFO", message)
            }
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(logInterceptor)
            clientBuilder.addInterceptor(ShakeNetworkInterceptor())
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
                .client(clientBuilder.build()).baseUrl(BuildConfig.NEW_API_URL).build().create(NewApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptorNew(appData: AppData): AuthInterceptorNew {
        return AuthInterceptorNew(appData)
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
