package com.example.ui.base.bottomSheet

import com.example.data.AppData
import com.example.data.models.ApiError
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import moxy.MvpPresenter
import retrofit2.HttpException

abstract class BaseBottomSheetPresenter<V : BaseBottomSheetContract.View>(
    private val appData: AppData
) : MvpPresenter<V>(), BaseBottomSheetContract.Presenter {

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


    fun getUserData() = appData.getUser()

    fun getHasBase() = appData.hasBaseState

    private fun createOnErrorConsumer(onError: ((Throwable) -> Unit)?): Consumer<Throwable> {
        return Consumer {
            if (onError != null) onError(it)
            else {
                if (it is ApiError) {
                    it.printStackTrace()
                } else if (it is HttpException) {
                    it.printStackTrace()
                }
            }
        }
    }

    fun Completable.subscribeSimple(
        onError: ((Throwable) -> Unit)? = null,
        onComplete: () -> Unit
    ): Disposable {
        return subscribe(
            Action(onComplete),
            createOnErrorConsumer(onError)
        )
    }

    fun <T> Single<T>.subscribeSimple(
        onError: ((Throwable) -> Unit)? = null,
        onSuccess: (T) -> Unit
    ): Disposable {
        return subscribe(
            Consumer(onSuccess),
            createOnErrorConsumer(onError)
        )
    }

    fun <T> Maybe<T>.subscribeSimple(
        onError: ((Throwable) -> Unit)? = null,
        onSuccess: (T) -> Unit
    ): Disposable {
        return subscribe(
            Consumer(onSuccess),
            createOnErrorConsumer(onError)
        )
    }

    fun <T> Observable<T>.subscribeSimple(
        onError: ((Throwable) -> Unit)? = null,
        onNext: (T) -> Unit
    ): Disposable {
        return subscribe(
            Consumer(onNext),
            createOnErrorConsumer(onError)
        )
    }


    fun <T> Flowable<T>.subscribeSimple(
        onError: ((Throwable) -> Unit)? = null,
        onNext: (T) -> Unit
    ): Disposable {
        return subscribe(
            Consumer(onNext),
            createOnErrorConsumer(onError)
        )
    }

}