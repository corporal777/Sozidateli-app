package com.example.ui.user.phoneconfirm

import com.arellomobile.mvp.InjectViewState
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
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
    }

    lateinit var phone: String

    private val timerCompositeDisposable = CompositeDisposable()
    private val smsCompositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += timerCompositeDisposable
        compositeDisposable += smsCompositeDisposable
        sendSms()
    }

    private fun sendSms() {
        viewState.apply {
            setCanResend(false)
            setTimeLeft(null)
        }

        smsCompositeDisposable += userRepository.checkPhone(phone)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    startTimer()
                }, {
                    it.printStackTrace()
                    viewState.apply {
                        showRequestErrorMessage()
                        setCanResend(true)
                    }
                })
    }

    override fun onResendClick() {
        smsCompositeDisposable.clear()
        sendSms()
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
        viewState.setTimeLeft("$secondsLeft")
    }
}
