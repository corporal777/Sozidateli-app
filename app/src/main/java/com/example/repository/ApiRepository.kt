package com.example.repository

import com.example.data.AppData
import com.example.data.models.ApiResponse
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
        return request.doOnError { saveSession(parseApiResponseError(it)) }
    }

    fun <T> call(request: Single<ApiResponse<T>>): Single<T> {
        return request
                .doOnError { saveSession(parseApiResponseError(it)) }
                .doOnSuccess { saveSession(it) }
                .map { it.response }
    }

    fun <T> call(request: Maybe<ApiResponse<T>>): Maybe<T> {
        return request
                .doOnError { saveSession(parseApiResponseError(it)) }
                .doOnSuccess { saveSession(it) }
                .map { it.response }
    }

    fun <T> call(request: Observable<ApiResponse<T>>): Observable<T> {
        return request
                .doOnError { saveSession(parseApiResponseError(it)) }
                .doOnNext { saveSession(it) }
                .map { it.response }
    }

    fun <T> call(request: Flowable<ApiResponse<T>>): Flowable<T> {
        return request
                .doOnError { saveSession(parseApiResponseError(it)) }
                .doOnNext { saveSession(it) }
                .map { it.response }
    }

    fun <T, C : List<T>> callPagination(request: Maybe<ApiResponse<C>>): Maybe<PaginationResponse<T>> {
        return request
                .doOnError { saveSession(parseApiResponseError(it)) }
                .doOnSuccess { saveSession(it) }
                .map {
                    PaginationResponse(
                            it.response_detail?.total,
                            it.response
                    )
                }
    }

    private fun saveSession(response: ApiResponse<*>?) {
        response?.session?.run { appData.token = token }
    }

    private fun parseApiResponseError(throwable: Throwable): ApiResponse<*>? {
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
                    return Gson().fromJson(body, ApiResponse::class.java)
                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                }
            }
        }

        return null
    }
}