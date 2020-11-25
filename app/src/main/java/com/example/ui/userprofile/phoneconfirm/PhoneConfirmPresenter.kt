package com.example.ui.userprofile.phoneconfirm

import com.arellomobile.mvp.InjectViewState
import com.example.BuildConfig
import com.example.data.AppData
import com.example.data.models.ApiError
import com.example.data.models.asOptional
import com.example.data.models.user.User
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.TimerFormatter
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class PhoneConfirmPresenter
@Inject constructor(
        private val appData: AppData,
        private val userRepository: UserRepository
) : BasePresenter<PhoneConfirmContract.View>(), PhoneConfirmContract.Presenter {

    companion object {
        private const val TIMER_SECONDS_COUNT = 180

        private const val WRONG_CODE_MESSAGE = "User has already confirmed phone or code mismatch"
    }

    lateinit var phone: String
    lateinit var password: String

    private val timerCompositeDisposable = CompositeDisposable()
    private val smsCompositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setPhone(phone)
        compositeDisposable += timerCompositeDisposable
        compositeDisposable += smsCompositeDisposable
        sendSms()
    }

    override fun onResendClick() {
        smsCompositeDisposable.clear()
        sendSms()
    }

    private fun sendSms() {
        viewState.apply {
            setCanResend(false)
            setTimeLeft(null)
        }

        val updateMap = mapOf(
                User.FIELD_USER_STATUS_PHONE to phone,
                User.FIELD_USER_PHONE_MOBILE to phone
        )
        smsCompositeDisposable += userRepository.updateUser(updateMap)
                .doOnSuccess {
                    val user = appData.getUser().apply {
                        user_phone = phone
                        user_status_phone = phone
                    }
                    appData.userChangeSubject.onNext(user.asOptional())
                }
                .flatMapCompletable {
                    /*if (BuildConfig.NEW_PROFILE_EDIT) {
                        Completable.complete()
                    } else {
                        userRepository.sendStatusPhoneConfirmSms(password)
                    }*/
                    userRepository.sendStatusPhoneConfirmSms(password)
                }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeBy(
                        onError = {
                            it.printStackTrace()
                            viewState.showSendSmsError()
                            viewState.setCanResend(true)
                        },
                        onComplete = {
                            startTimer()
                        }
                )
    }

    private fun startTimer() {
        timerCompositeDisposable.clear()
        viewState.setCanResend(false)
        setTimeLeft(TIMER_SECONDS_COUNT)

        timerCompositeDisposable += Observable.interval(1000, TimeUnit.MILLISECONDS)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val timeLeft = TIMER_SECONDS_COUNT - (it.toInt() + 1)
                    if (timeLeft < 0) {
                        timerCompositeDisposable.clear()
                        viewState.setCanResend(true)
                        viewState.setTimeLeft(null)
                    } else {
                        setTimeLeft(timeLeft)
                    }
                }, {
                    it.printStackTrace()
                })
    }

    private fun setTimeLeft(secondsLeft: Int) {
        viewState.setTimeLeft(TimerFormatter.formatMinutes(secondsLeft * 1000L))
    }

    override fun onCodeSendClick(code: String) {
        if (BuildConfig.NEW_PROFILE_EDIT) {
            if (code == "123456") {
                appData.userPhoneConfirmedSubject.onNext(true)
                viewState.onPhoneConfirmationComplete()
            } else {
                compositeDisposable += userRepository.sendStatusPhoneConfirmCode(code)
                        .performOnBackgroundOutOnMain()
                        .withLoadingDialog(viewState)
                        .subscribeBy(
                                onError = {
                                    viewState.onPhoneConfirmationComplete()
                                },
                                onComplete = {
                                    val user = appData.getUser()
                                    user.user_phone_confirmed = true
                                    appData.userChangeSubject.onNext(user.asOptional())
                                    viewState.onPhoneConfirmationComplete()
                                }
                        )
                //viewState.showWrongCodeError()
            }
        } else {
            compositeDisposable += userRepository.sendStatusPhoneConfirmCode(code)
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeBy(
                            onError = {
                                if (it is ApiError && it.errors.contains(WRONG_CODE_MESSAGE)) {
                                    viewState.showWrongCodeError()
                                } else {
                                    it.printStackTrace()
                                    viewState.showRequestErrorMessage()
                                }
                            },
                            onComplete = {
                                val user = appData.getUser()
                                user.user_phone_confirmed = true
                                appData.userChangeSubject.onNext(user.asOptional())
                                viewState.onPhoneConfirmationComplete()
                            }
                    )
        }
    }
}
