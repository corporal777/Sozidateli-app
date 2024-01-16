package com.example.ui.auth.recoveryPassword

import call
import com.example.data.AppData
import com.example.data.bodies.RecoverPasswordBody
import com.example.repository.AuthRepository
import com.example.ui.auth.register.email.finish.FinishRegisterPresenter
import com.example.ui.base.BasePresenter
import com.example.util.AuthValidateUtil
import com.example.util.Utils
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
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
    private var loginType = "email"

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setEmail(email)
    }

    override fun onRecoveryClick() {
        if (isDataValid()) {
            val login = if (loginType == "email") email else Utils.validatePhoneBeforeSend(email)
            compositeDisposable += authRepository.sendRecoveryEmail(loginType, login)
                .performOnBackgroundOutOnMain()
                .withCustomLoading(viewState)
                .subscribeSimple(
                    onError = { viewState.showEmailError(true) },
                    onSuccess = {
                        if (loginType == "email") viewState.showEmailRecovery(login, it.userId)
                        else viewState.showPhoneRecovery(login, it.userId)
                    }
                )
        } else viewState.showEmailError(true)
    }


    override fun onChangeEmailText(email: String) {
        this.email = email
        performDataChange()
        viewState.showEmailError(false)
    }


    private fun isDataValid(): Boolean {
        return if (Utils.isPhone(email) && !Utils.isContainLetters(email)) {
            loginType = "phone"
            Utils.isPhoneNumberValid(email)
        } else {
            loginType = "email"
            AuthValidateUtil.isValidEmail(email)
        }
    }


    private fun performDataChange() = viewState.enableRecoveryBtn(isDataValid())

    companion object {
        private const val USER_NOT_REGISTERED_ERROR = "User is not registered yet"
    }
}
