package com.example.ui.userprofile.read.settings.confirm_phone_email

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.ConfirmCodeBody
import com.example.data.bodies.EmailCodeBody
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.register.email.finishregister.FinishRegisterPresenter
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class ConfirmEmailPhonePresenter
@Inject constructor(
    private val appData: AppData,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : BaseBottomSheetPresenter<ConfirmEmailPhoneContract.View>(appData),
    ConfirmEmailPhoneContract.Presenter {

    private val timerCompositeDisposable = CompositeDisposable()
    var mobilePhone = ""
    var loginType = ""

    private var isFirstLaunch = true
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += timerCompositeDisposable
        viewState.showProgressLoading()
        sendCode()
    }

    override fun sendCode() {
        compositeDisposable += if (loginType == "email") {
            authRepository.registerEmailResend(mobilePhone)
        } else {
            authRepository.registerPhoneResend("personal", mobilePhone)
        }
            .performOnBackgroundOutOnMain()
            .let {
                if (!isFirstLaunch) it.withCustomProgressBarLoadingDialog(viewState)
                else it
            }
            .subscribeSimple(
                onError = {
                    startTimerForResendCode()
                    if (isFirstLaunch) {
                        viewState.apply {
                            hideProgressLoading()
                            setContentType(loginType)
                        }
                        isFirstLaunch = false
                    }
                },
                onComplete = {
                    startTimerForResendCode()
                    if (isFirstLaunch) {
                        viewState.apply {
                            hideProgressLoading()
                            setContentType(loginType)
                        }
                        isFirstLaunch = false
                    }
            })
    }

    override fun startTimerForResendCode() {
        timerCompositeDisposable.clear()
        timerCompositeDisposable += Observable.interval(1000, TimeUnit.MILLISECONDS)
            .performOnBackgroundOutOnMain()
            .subscribe({
                val timeLeft = 60 - (it.toInt() + 1)
                viewState.apply {
                    setButtonSendAgain(false)
                    setTimeLeft(timeLeft)
                }
                if (timeLeft < 0) {
                    viewState.setButtonSendAgain(true)
                    timerCompositeDisposable.clear()
                }
            }, {
                it.printStackTrace()
            })
    }

    override fun confirmEmailPhone(email: String, code: String) {
        compositeDisposable += if (loginType == "email") {
            userRepository.confirmEmailCodeNew(
                appData.getId(),
                EmailCodeBody(code = code, email = email)
            )
        } else {
            authRepository.confirmPhone(ConfirmCodeBody("personal", email, code))
        }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.setCodeError(true)
                },
                onComplete = {
                    viewState.setEmailPhoneIsConfirmed()
                })
    }

}