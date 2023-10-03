package com.example.ui.base

import com.arellomobile.mvp.MvpPresenter
import com.example.data.AppData
import com.example.data.models.ApiError
import com.example.exceptions.NoInternetConnectionException
import com.example.ui.views.StateType
import com.google.gson.Gson
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withCheckInternetConnectivity
import javax.inject.Inject

open class BasePresenter<V : BaseContract.View>
@Inject constructor(val appDat: AppData) : MvpPresenter<V>(), BaseContract.Presenter {

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


    fun getUserData() = appDat.getUserNew()

    fun getHasBase() = appDat.hasBaseState

    private fun createOnErrorConsumer(
        onError: ((Throwable) -> Unit)?,
        onNoInternetConnectionException: (() -> Unit)?,
        onApiError: ((ApiError) -> Unit)?
    ): Consumer<Throwable> {
        return Consumer {

            if (it is NoInternetConnectionException) if (onNoInternetConnectionException != null) onNoInternetConnectionException() else onReceiveNoInternetError()
            else if (it is ApiError) if (onApiError != null) onApiError(it) else onReceiveApiError(
                it
            )

            if (onError != null) onError(it)
            else {
                if (it is ApiError) {
                    when (it.code) {
                        409 -> viewState.showEmailErrorMessage()
                        else -> {
                            val errors = (it as ApiError).errors
                            if (errors.isNullOrEmpty()) {
                                onReceiveError(it)
                            } else {
                                when (errors[0]) {
                                    "User with same email exists" -> viewState.showEmailErrorMessage()
                                    "User is not in MAX PROTECTION" -> viewState.showNotificationErrorMessage()
                                    "such phone already registered" -> viewState.showPhoneErrorMessage()
                                    else -> onReceiveError(it)
                                }
                            }
                        }
                    }
                } else if (it is HttpException) {
                    when (it.code()) {
                        400 -> {
                            try {
                                val error = Gson().fromJson(
                                    it.response()?.errorBody()?.string(),
                                    NewErrors::class.java
                                )
                                when (error.errors[0].message) {
                                    "The event activity has ended" -> {
                                        val message = "Событие уже прошло"
                                        viewState.showErrorMessage(false, message)
                                    }
                                }
                            } catch (e: Exception) {
                            }

                        }
                        409 -> {
                            try {
                                val error = Gson().fromJson(
                                    it.response()?.errorBody()?.string(),
                                    NewErrors::class.java
                                )
                                when (error.errors[0].message) {
                                    "User with same email exists" -> viewState.showEmailErrorMessage()
                                    "User is not in MAX PROTECTION" -> viewState.showNotificationErrorMessage()
                                    "such phone already registered" -> viewState.showPhoneErrorMessage()
                                    else -> onReceiveError(it)
                                }
                            } catch (e: Exception) {

                            }
                        }
                        401 -> {}
                        403 -> {
                            try {
                                val error = Gson().fromJson(
                                    it.response()?.errorBody()?.string(),
                                    NewErrors::class.java
                                )
                                when (error.errors[0].message) {
                                    "your profile level is to low, basic required" -> viewState.showStateErrorMessage(
                                        StateType.BASE,
                                        getHasBase(),
                                        getUserData()
                                    )
                                    "your profile level is to low, maximum required" -> viewState.showStateErrorMessage(
                                        StateType.MAX,
                                        getHasBase(),
                                        getUserData()
                                    )
                                    else -> onReceiveError(it)
                                }
                            } catch (e: Exception) {

                            }
                        }
                    }
                }
            }
        }
    }

    data class NewErrors(val errors: List<NewError>)
    data class NewEventErrors(val errors: List<NewBannedOrCancelledError>)
    data class Errors(val errors: List<ErrorModel>)
    data class ErrorModel(
        val code: String? = null,
        val field: String? = null,
        val message: String? = null
    )

    data class NewError(
        val code: String? = null,
        val type: String? = null,
        val profileLevelRequired: String? = null,
        val message: String? = null,
        val additionalData: EventAdditionalDataError? = null
    )

    data class NewBannedOrCancelledError(
        val code: String? = null,
        val type: String? = null,
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
