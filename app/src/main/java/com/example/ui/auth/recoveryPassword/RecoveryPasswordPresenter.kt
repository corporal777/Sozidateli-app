package com.example.ui.auth.recoveryPassword

import call
import com.example.data.AppData
import com.example.data.bodies.RecoverPasswordBody
import com.example.data.models.ApiError
import com.example.repository.AuthRepository
import com.example.ui.auth.register.email.finish.FinishRegisterPresenter
import com.example.ui.base.BasePresenter
import com.example.util.AuthValidateUtil
import com.example.util.Utils
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withCustomLoading
import withProgressBarDialogLoading
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class RecoveryPasswordPresenter
@Inject constructor(
    private val authRepository: AuthRepository,
    appData: AppData
) : BasePresenter<RecoveryPasswordContract.View>(appData), RecoveryPasswordContract.Presenter {

    var email = ""
    var loginType = "email"

    private val timerCompositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += timerCompositeDisposable
        viewState.setEmail(email)
    }

    override fun onRecoveryClick() {
        if (isDataValid()) {
            compositeDisposable += Maybe.just(
                if (loginType == "email") email else Utils.validatePhoneBeforeSend(email)
            )
                .flatMap { authRepository.sendRecoveryEmail(loginType, it) }
                .performOnBackgroundOutOnMain()
                .withCustomLoading(viewState)
                .subscribeSimple(
                    onError = { catchError(it) },
                    onSuccess = {
                        viewState.showRecoveryNotification(email, it.userId)
                        if (loginType != "email") startTimer()
                    }
                )
        } else viewState.showEmailError(true)
    }

    override fun sendCodeAgain() {
        val phone = Utils.validatePhoneBeforeSend(email)
        compositeDisposable += authRepository.sendRecoveryEmail(loginType, phone)
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribe({
                startTimer()
            }, { it.printStackTrace() })
    }

    private fun startTimer() {
        timerCompositeDisposable.clear()
        viewState.setTimeLeft(FinishRegisterPresenter.TIMER_SECONDS_COUNT)
        timerCompositeDisposable += Observable.interval(1000, TimeUnit.MILLISECONDS)
            .performOnBackgroundOutOnMain()
            .subscribe({
                val timeLeft = FinishRegisterPresenter.TIMER_SECONDS_COUNT - (it.toInt() + 1)
                viewState.setTimeLeft(timeLeft)
                if (timeLeft < 0) {
                    timerCompositeDisposable.clear()
                }
            }, {
                it.printStackTrace()
            })
    }

    override fun onChangeEmailText(email: String) {
        viewState.showEmailError(false)
        this.email = email
        performDataChange()
    }


    private fun isDataValid(): Boolean {
        return if (Utils.isPhone(email) && !Utils.isContainLetters(email)) {
            loginType = "phone"
            Utils.newPhoneValidator(email)
        } else {
            loginType = "email"
            AuthValidateUtil.isValidEmail(email)
        }
    }

    override fun onSetPassword(code: String, password: String, userId: String) {
        viewState.setIgnoreTokenListener(true)
        authRepository.recoverPasswordNew(
            RecoverPasswordBody("phone", code, password, userId)
        )
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    viewState.setIgnoreTokenListener(false)
                }, onComplete = {
                    viewState.apply {
                        setIgnoreTokenListener(false)
                        showPasswordSuccessUpdated()
                        navigateUp()
                    }
                }
            ).call(compositeDisposable)
    }

    private fun performDataChange() = viewState.enableRecoveryBtn(isDataValid())
    override fun onCloseClick() = viewState.navigateUp()

    private fun catchError(it: Throwable) {
        if (it is HttpException) viewState.showWrongEmailError()
        else if ((it as? ApiError)?.hasError(USER_NOT_REGISTERED_ERROR) == true) {
            viewState.showWrongEmailError()
        } else onReceiveError(it)
    }

    companion object {
        private const val USER_NOT_REGISTERED_ERROR = "User is not registered yet"
    }
}
