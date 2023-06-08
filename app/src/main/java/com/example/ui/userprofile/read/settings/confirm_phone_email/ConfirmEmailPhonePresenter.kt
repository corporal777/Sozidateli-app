package com.example.ui.userprofile.read.settings.confirm_phone_email

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.ConfirmCodeBody
import com.example.data.bodies.EmailCodeBody
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.example.util.Utils
import io.reactivex.Completable
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

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += timerCompositeDisposable
        viewState.apply {
            setContentType(loginType)
            setButtonSendAgain(false)
        }
        startTimer()
    }

    override fun sendCodeAgain() {
        compositeDisposable += if (loginType == "email") {
            authRepository.registerEmailResend(mobilePhone)
        } else {
            authRepository.registerPhoneResend("personal", mobilePhone)
        }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                startTimer()
            }
    }


    private fun startTimer() {
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
        compositeDisposable += Completable.create { emitter ->
            val disposable = CompositeDisposable()
            if (loginType == "email") {
                authRepository.confirmEmailCode(EmailCodeBody(code = code, email = email))
                    .subscribeSimple(
                        onError = { emitter.onError(it) },
                        onComplete = {
                            appData.updateUserNew {
                                this.email?.value = email
                                this.email?.isConfirmed = true
                                this.email?.onConfirmation = null
                            }
                            emitter.onComplete()
                        }
                    ).call(disposable)
            } else {
                authRepository.confirmPhoneCode(ConfirmCodeBody("personal", email, code))
                    .subscribeSimple(
                        onError = { emitter.onError(it) },
                        onComplete = { emitter.onComplete() }
                    ).call(disposable)
            }
            emitter.setDisposable(disposable)
        }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.setCodeError(true)
                }, onComplete = {
                    viewState.setEmailPhoneIsConfirmed()
                })
    }

    fun initLoginType(email : String){
        this.mobilePhone = email
        loginType = if (Utils.isPhone(email) && !Utils.isContainLetters(email)) "phone"
        else "email"
    }
}