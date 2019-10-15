package com.example.repository

import com.example.data.AppData
import com.example.data.models.ApiResponse
import com.example.util.ApiErrorParser
import com.example.util.pagination.PaginationResponse
import io.reactivex.*

abstract class ApiRepository(
        private val appData: AppData
) {

    fun call(request: Completable): Completable {
        return request
                .onErrorResumeNext { t: Throwable -> Completable.error(processError(t)) }
    }

    fun <T> call(request: Single<ApiResponse<T>>): Single<T> {
        return request
                .onErrorResumeNext { t: Throwable -> Single.error(processError(t)) }
                .doOnSuccess { saveSession(it) }
                .map { it.response }
    }


    fun <T> call(request: Maybe<ApiResponse<T>>): Maybe<T> {
        return request
                .onErrorResumeNext { t: Throwable -> Maybe.error(processError(t)) }
                .doOnSuccess { saveSession(it) }
                .map { it.response }
    }

    fun <T> call(request: Observable<ApiResponse<T>>): Observable<T> {
        return request
                .onErrorResumeNext { t: Throwable -> Observable.error(processError(t)) }
                .doOnNext { saveSession(it) }
                .map { it.response }
    }

    fun <T> call(request: Flowable<ApiResponse<T>>): Flowable<T> {
        return request
                .onErrorResumeNext { t: Throwable -> Flowable.error(processError(t)) }
                .doOnNext { saveSession(it) }
                .map { it.response }
    }

    fun <T, C : List<T>> callPagination(request: Maybe<ApiResponse<C>>): Maybe<PaginationResponse<T>> {
        return request
                .onErrorResumeNext { t: Throwable -> Maybe.error(processError(t)) }
                .doOnSuccess { saveSession(it) }
                .map {
                    PaginationResponse(
                            it.response_detail?.total,
                            it.response
                    )
                }
    }

    private fun saveSession(response: ApiResponse<*>?) {
        response?.session?.token?.let { saveSession(it) }
    }

    private fun saveSession(token: String) {
        appData.token = token
    }

    private fun processError(throwable: Throwable): Throwable {
        return ApiErrorParser.parse(throwable)?.apply {
            session?.token?.let { saveSession(it) }
        } ?: throwable
    }
}