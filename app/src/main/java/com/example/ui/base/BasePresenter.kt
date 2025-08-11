package com.example.ui.base

import com.examle.data.AppData
import com.examle.data.models.ApiError
import com.example.exceptions.EmptyDataException
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import moxy.MvpPresenter
import javax.inject.Inject

open class BasePresenter<V : BaseContract.View>
@Inject constructor(private val appData: AppData) : MvpPresenter<V>(), BaseContract.Presenter {

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

    protected open fun onReceivePagingError(error: Throwable) {
        if (error !is EmptyDataException) onReceiveError(error)
    }

    protected open fun onReceiveNoInternetError() {
        hasNoConnectionError = true
    }

    protected open fun onReceiveApiError(apiError: ApiError) {
    }



    fun getUserData() = appData.getUser()
    fun getEducationLevels() = appData.educationLevels
    fun getAcademicDegrees() = appData.academicDegrees
    fun getSpecialities() = appData.specialities

    fun getHasBase() = appData.hasBaseState
    fun getHasMax() = appData.hasMaxState
    fun isStoriesShown() = appData.isStoriesShown
    fun isTemporaryUser() = appData.isTemporaryUser()

    private fun createOnErrorConsumer(
        onError: ((Throwable) -> Unit)?,
        onNoInternetConnectionException: (() -> Unit)?,
        onApiError: ((ApiError) -> Unit)?
    ): Consumer<Throwable> {
        return Consumer {
        }
    }

    data class NewErrors(val errors: List<NewError>) {

    }
    data class NewError(
        val code: String? = null,
        val type: String? = null,
        val profileLevelRequired: String? = null,
        val message: String? = null,
        val additionalData: EventAdditionalDataError? = null
    )
    data class EventAdditionalDataError(
        val id: Int? = null,
        val name: String? = null,
    )


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
