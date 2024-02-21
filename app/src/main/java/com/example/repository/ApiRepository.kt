package com.example.repository

import com.example.data.AppData
import com.example.data.models.ApiResponse
import com.example.exceptions.NoInternetConnectionException
import com.example.util.ApiErrorParser
import com.example.util.pagination.PaginationResponse
import io.reactivex.*
import retrofit2.HttpException
import java.net.ConnectException

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
                .map { it.response }
    }

    fun <T> call(request: Maybe<ApiResponse<T>>): Maybe<T> {
        return request
                .onErrorResumeNext { t: Throwable -> Maybe.error(processError(t)) }
                .map { it.response }
    }

    fun <T> call(request: Observable<ApiResponse<T>>): Observable<T> {
        return request
                .onErrorResumeNext { t: Throwable -> Observable.error(processError(t)) }
                .map { it.response }
    }

    fun <T> call(request: Flowable<ApiResponse<T>>): Flowable<T> {
        return request
                .onErrorResumeNext { t: Throwable -> Flowable.error(processError(t)) }

                .map { it.response }
    }

    fun <T, C : List<T>> callPagination(request: Maybe<ApiResponse<C>>): Maybe<PaginationResponse<T>> {
        return request
                .onErrorResumeNext { t: Throwable -> Maybe.error(processError(t)) }
                .map {
                    PaginationResponse(
                            it.response_detail?.total,
                            it.response
                    )
                }
    }

    private fun processError(throwable: Throwable): Throwable {
        if (throwable is ConnectException) return NoInternetConnectionException()

        return ApiErrorParser.parse(throwable)?.apply {
            if ((throwable as HttpException).code() == 409)
                code = 409
        } ?: throwable
    }
}