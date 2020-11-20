package com.example.ui.base

import com.arellomobile.mvp.MvpPresenter
import com.example.data.models.ApiError
import com.example.exceptions.NoInternetConnectionException
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import javax.inject.Inject

open class BasePresenter<V : BaseContract.View>
@Inject constructor()
    : MvpPresenter<V>(), BaseContract.Presenter {

    protected val compositeDisposable = CompositeDisposable()
    protected var hasNoConnectionError = false

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    protected open fun onReceiveError(error: Throwable) {
        error.printStackTrace()
        viewState.showRequestErrorMessage()
    }

    protected open fun onReceiveNoInternetError() {
        hasNoConnectionError = true
    }

    protected open fun onReceiveApiError(apiError: ApiError) {

    }

    fun checkInternetAndRun(onComplete: () -> Unit): Disposable {
        return Completable.complete()
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .subscribeSimple(
                        onError = {
                            if (it !is NoInternetConnectionException) throw it
                        },
                        onComplete = onComplete
                )
    }

    private fun createOnErrorConsumer(
            onError: ((Throwable) -> Unit)?,
            onNoInternetConnectionException: (() -> Unit)?,
            onApiError: ((ApiError) -> Unit)?
    ): Consumer<Throwable> {
        return Consumer {
            if (it is NoInternetConnectionException) if (onNoInternetConnectionException != null) onNoInternetConnectionException() else onReceiveNoInternetError()
            else if (it is ApiError) if (onApiError != null) onApiError(it) else onReceiveApiError(it)

            if (onError != null) onError(it)
            else {
                val errors = (it as ApiError).errors
                if (errors.isNullOrEmpty()) {
                    onReceiveError(it)
                } else {
                    when (errors[0]) {
                        "User with same email exists" -> viewState.showEmailErrorMessage()
                        "User is not in MAX PROTECTION" -> viewState.showNotificationErrorMessage()
                        else -> onReceiveError(it)
                    }
                }
            }
        }
    }

    fun Completable.subscribeSimple(
            onError: ((Throwable) -> Unit)? = null,
            onNoInternetConnectionException: (() -> Unit)? = null,
            onApiError: ((ApiError) -> Unit)? = null,
            onComplete: () -> Unit
    ): Disposable {
        return subscribe(
                Action(onComplete),
                createOnErrorConsumer(onError, onNoInternetConnectionException, onApiError)
        )
    }

    fun <T> Single<T>.subscribeSimple(
            onError: ((Throwable) -> Unit)? = null,
            onNoInternetConnectionException: (() -> Unit)? = null,
            onApiError: ((ApiError) -> Unit)? = null,
            onSuccess: (T) -> Unit
    ): Disposable {
        return subscribe(
                Consumer(onSuccess),
                createOnErrorConsumer(onError, onNoInternetConnectionException, onApiError)
        )
    }

    fun <T> Maybe<T>.subscribeSimple(
            onError: ((Throwable) -> Unit)? = null,
            onNoInternetConnectionException: (() -> Unit)? = null,
            onApiError: ((ApiError) -> Unit)? = null,
            onSuccess: (T) -> Unit
    ): Disposable {
        return subscribe(
                Consumer(onSuccess),
                createOnErrorConsumer(onError, onNoInternetConnectionException, onApiError)
        )
    }

    fun <T> Observable<T>.subscribeSimple(
            onError: ((Throwable) -> Unit)? = null,
            onNoInternetConnectionException: (() -> Unit)? = null,
            onApiError: ((ApiError) -> Unit)? = null,
            onNext: (T) -> Unit
    ): Disposable {
        return subscribe(
                Consumer(onNext),
                createOnErrorConsumer(onError, onNoInternetConnectionException, onApiError)
        )
    }

    fun <T> Flowable<T>.subscribeSimple(
            onError: ((Throwable) -> Unit)? = null,
            onNoInternetConnectionException: (() -> Unit)? = null,
            onApiError: ((ApiError) -> Unit)? = null,
            onNext: (T) -> Unit
    ): Disposable {
        return subscribe(
                Consumer(onNext),
                createOnErrorConsumer(onError, onNoInternetConnectionException, onApiError)
        )
    }
}
