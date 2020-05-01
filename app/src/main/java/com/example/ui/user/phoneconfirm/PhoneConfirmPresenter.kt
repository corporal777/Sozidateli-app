package com.example.ui.user.phoneconfirm

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.ApiError
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

//        val updateMap = mapOf(User.FIELD_USER_STATUS_PHONE to phone)
//        smsCompositeDisposable += userRepository.updateUser(updateMap)
//                .flatMapCompletable { userRepository.sendStatusPhoneConfirmSms(password) }
        smsCompositeDisposable += Completable.timer(1, TimeUnit.SECONDS)
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
        compositeDisposable += Completable.timer(1, TimeUnit.SECONDS)
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
                            viewState.onPhoneConfirmationComplete()
                        }
                )
    }
}
