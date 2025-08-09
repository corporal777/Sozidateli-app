package com.example.ui.base

import com.examle.data.AppData
import com.example.data.models.ApiError
import com.example.data.models.EventNew
import com.example.exceptions.EmptyDataException
import com.example.common.exceptions.NoInternetConnectionException
import com.example.ui.views.dialogs.StateType
import com.google.gson.Gson
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import moxy.MvpPresenter
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withCheckInternetConnectivity
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


    fun getUserData() = appData.getUser()
    fun getEducationLevels() = appData.educationLevels
    fun getAcademicDegrees() = appData.academicDegrees
    fun getSpecialities() = appData.specialities

    fun getHasBase() = appData.hasBaseState
    fun getHasMax() = appData.hasMaxState
    fun isStoriesShown() = appData.isStoriesShown
    fun isTemporaryUser() = appData.isTemporaryUser()

    fun isProfileLevelLow(event : EventNew) : Boolean {
        val state = event.binds?.currentUserRegistrationState ?: return true
        return if (state.prohibitions?.profileLevelToLow?.requiredLevel == "basic") !getHasBase()
        else !getHasMax()
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
                if (it is ApiError) {
                    when (it.code) {
                        409 -> viewState.showEmailErrorMessage()
                        else -> {
                            val errors = it.errors
                            if (errors.isNullOrEmpty()) {
                                onReceiveError(it)
                            } else {
                                when (errors[0]) {
                                    "User with same email exists" -> viewState.showEmailErrorMessage()
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

    data class NewErrors(val errors: List<NewError>) {
        companion object {
            fun fromJson(t : HttpException): NewErrors? {
                return Gson().fromJson(t.response()?.errorBody()?.string(), NewErrors::class.java)
            }
        }
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
