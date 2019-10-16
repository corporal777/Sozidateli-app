package com.example.ui.base

import com.arellomobile.mvp.MvpPresenter
import com.example.data.models.ApiError
import com.example.extensions.NoInternetConnectionException
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

    protected var hasNoInternetError = false

    protected val compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    protected open fun onReceiveError(error: Throwable) {

    }

    protected open fun onReceiveNoInternetError() {
        hasNoInternetError = true
        viewState.showNoConnectionMessage()
    }

    protected open fun onReceiveApiError(apiError: ApiError) {

    }

    fun checkInternetAndRun(onComplete: () -> Unit): Disposable {
        return Completable.complete()
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .subscribeSimple(onError = {
                    if (it !is NoInternetConnectionException) throw it
                }, onComplete = onComplete)
    }

    private fun createOnErrorConsumer(onError: ((Throwable) -> Unit)?): Consumer<Throwable> {
        return Consumer {
            if (it is NoInternetConnectionException) onReceiveNoInternetError()
            else if (it is ApiError) onReceiveApiError(it)

            if (onError != null) onError(it)
            else onReceiveError(it)
        }
    }

    fun Completable.subscribeSimple(onError: ((Throwable) -> Unit)? = null, onComplete: () -> Unit): Disposable {
        return subscribe(Action(onComplete), createOnErrorConsumer(onError))
    }

    fun <T> Single<T>.subscribeSimple(onError: ((Throwable) -> Unit)? = null, onSuccess: (T) -> Unit): Disposable {
        return subscribe(Consumer(onSuccess), createOnErrorConsumer(onError))
    }

    fun <T> Maybe<T>.subscribeSimple(onError: ((Throwable) -> Unit)? = null, onSuccess: (T) -> Unit): Disposable {
        return subscribe(Consumer(onSuccess), createOnErrorConsumer(onError))
    }

    fun <T> Observable<T>.subscribeSimple(onError: ((Throwable) -> Unit)? = null, onNext: (T) -> Unit): Disposable {
        return subscribe(Consumer(onNext), createOnErrorConsumer(onError))
    }

    fun <T> Flowable<T>.subscribeSimple(onError: ((Throwable) -> Unit)? = null, onNext: (T) -> Unit): Disposable {
        return subscribe(Consumer(onNext), createOnErrorConsumer(onError))
    }
}
