package com.example.ui.userprofile.phoneconfirm

import com.arellomobile.mvp.InjectViewState
import com.example.BuildConfig
import com.example.data.AppData
import com.example.data.bodies.ConfirmCodeBody
import com.example.data.bodies.PhoneCodeBody
import com.example.data.models.ApiError
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.data.models.asOptional
import com.example.data.models.user.User
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.userprofile.phoneconfirm.PhoneConfirmFragment.Companion.FROM_OTHER
import com.example.ui.userprofile.phoneconfirm.PhoneConfirmFragment.Companion.FROM_PROFILE
import com.example.util.PHONE_PERSONAL
import com.example.util.TimerFormatter
import com.example.util.phoneToServer
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
        private val userRepository: UserRepository,
        private val authRepository: AuthRepository
) : BasePresenter<PhoneConfirmContract.View>(), PhoneConfirmContract.Presenter {

    companion object {
        private const val TIMER_SECONDS_COUNT = 180

        private const val WRONG_CODE_MESSAGE = "User has already confirmed phone or code mismatch"
    }

    lateinit var phone: String
    lateinit var password: String
    var screenType = FROM_OTHER

    private val timerCompositeDisposable = CompositeDisposable()
    private val smsCompositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setPhone(phone)
        compositeDisposable += timerCompositeDisposable
        compositeDisposable += smsCompositeDisposable
        sendSms()
        startTimer()
    }

    override fun onResendClick() {
        smsCompositeDisposable.clear()
        sendSms()
        startTimer()
    }

    private fun sendSms() {
        viewState.apply {
            setCanResend(false)
            setTimeLeft(null)
        }

        compositeDisposable += authRepository.registerPhoneResend("personal", phone.phoneToServer()?: "")
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    startTimer()
                }, {
                    it.printStackTrace()
                    viewState.showSendSmsError()
                    viewState.setCanResend(true)
                })
        /*smsCompositeDisposable += userRepository.updateProfile(appData.getId(),
                mapOf(UserDetail.USER_PHONE to arrayListOf(FieldDetails(value = phone.phoneToServer(), type = PHONE_PERSONAL, isVisible = true, isConfirmed = false))))
                .doOnSuccess {
                    val user = appData.getUserNew().apply {
                        phone = it.phone
                    }
                    appData.userNewChangeSubject.onNext(user.asOptional())
                }
                .flatMapCompletable {
                    userRepository.sendPhoneCode(appData.getId(), phone)
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
                )*/
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
        compositeDisposable += authRepository.confirmPhone(ConfirmCodeBody("personal", phone, code))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    val user = appData.getUserNew()
                    user.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.isConfirmed = true
                    appData.userNewChangeSubject.onNext(user.asOptional())
                    appData.userPhoneConfirmedSubject.onNext(true)
                    viewState.onPhoneConfirmationComplete()
                }, {
                    if (it is ApiError && it.errors.contains(WRONG_CODE_MESSAGE)) {
                        viewState.showWrongCodeError()
                    } else {
                        it.printStackTrace()
                        viewState.showRequestErrorMessage()
                    }
                })
        /*compositeDisposable += userRepository.confirmPhoneCode(appData.getId(), PhoneCodeBody(phone, code))
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
                            val user = appData.getUserNew()
                            user.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.isConfirmed = true
                            appData.userNewChangeSubject.onNext(user.asOptional())
                            appData.userPhoneConfirmedSubject.onNext(true)
                            viewState.onPhoneConfirmationComplete()
                        }
                )*/
    }
}
