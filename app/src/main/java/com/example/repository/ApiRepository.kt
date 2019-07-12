package com.example.repository

import com.example.data.AppData
import com.example.data.models.ApiResponse
import com.example.data.prefs.AppPrefs
import com.example.util.ApiErrorParser
import com.example.util.pagination.PaginationResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.reactivex.*
import retrofit2.HttpException
import java.io.IOException

abstract class ApiRepository(
        private val appData: AppData
) {

    fun call(request: Completable): Completable {
        return request.doOnError { processError(it) }
    }

    fun <T> call(request: Single<ApiResponse<T>>): Single<T> {
        return request
                .doOnError { processError(it) }
                .doOnSuccess { saveSession(it) }
                .map { it.response }
    }


    fun <T> call(request: Maybe<ApiResponse<T>>): Maybe<T> {
        return request
                .doOnError { processError(it) }
                .doOnSuccess { saveSession(it) }
                .map { it.response }
    }

    fun <T> call(request: Observable<ApiResponse<T>>): Observable<T> {
        return request
                .doOnError { processError(it) }
                .doOnNext { saveSession(it) }
                .map { it.response }
    }

    fun <T> call(request: Flowable<ApiResponse<T>>): Flowable<T> {
        return request
                .doOnError { processError(it) }
                .doOnNext { saveSession(it) }
                .map { it.response }
    }

    fun <T, C : List<T>> callPagination(request: Maybe<ApiResponse<C>>): Maybe<PaginationResponse<T>> {
        return request
                .doOnError { processError(it) }
                .doOnSuccess { saveSession(it) }
                .map {
                    PaginationResponse(
                            it.response_detail?.total,
                            it.response
                    )
                }
    }

    protected fun saveSession(response: ApiResponse<*>?) {
        response?.session?.run { appData.token = token }
    }

    protected fun processError(throwable: Throwable){
        val response  = ApiErrorParser.parse(throwable)
        saveSession(response)
    }
}