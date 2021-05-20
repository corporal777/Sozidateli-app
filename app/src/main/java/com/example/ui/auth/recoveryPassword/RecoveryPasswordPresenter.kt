package com.example.ui.auth.recoveryPassword

import android.content.Context
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.bodies.RecoverPasswordBody
import com.example.data.models.ApiError
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.util.AuthValidateUtil
import com.example.util.Utils
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class RecoveryPasswordPresenter
@Inject constructor(
        private val authRepository: AuthRepository
) : BasePresenter<RecoveryPasswordContract.View>(), RecoveryPasswordContract.Presenter {

    companion object {
        private const val USER_NOT_REGISTERED_ERROR = "User is not registered yet"
    }

    var email = ""
    var loginType = "email"

    private var onUserUnderstandEverything = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.apply {
            setEmail(email)
        }
    }

    override fun onRecoveryClick(context: Context) {
        if (isDataValid(context)) {
            compositeDisposable += authRepository.sendRecoveryEmail(loginType, email)
                    .withCheckInternetConnectivity()
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple(
                            onError = {
                                if ((it as? ApiError)?.hasError(USER_NOT_REGISTERED_ERROR) == true) {
                                    viewState.showWrongEmailError()
                                } else {
                                    onReceiveError(it)
                                }
                            },
                            onComplete = {
                                viewState.showRecoveryNotification(email)
                            }
                    )
        } else {
            viewState.showEmailError(true)
        }
    }

    override fun onChangeEmailText(email: String, context: Context) {
        viewState.showEmailError(false)
        this.email = email
        performDataChange(context)
    }

    private fun performDataChange(context: Context) {
        viewState.enableRecoveryBtn(isDataValid(context))
    }

    private fun isDataValid(context: Context): Boolean {
        return if (Utils.isPhone(email) && !Utils.isContainLetters(email)) {
            loginType = "phone"
            Utils.newPhoneValidator(context, email)
        } else {
            loginType = "email"
            AuthValidateUtil.isValidEmail(email)
        }
        //return AuthValidateUtil.isValidEmail(email)
    }

    override fun onUserUnderstand() {
        if (!onUserUnderstandEverything) {
            onUserUnderstandEverything = true
            viewState.navigateUp()
        }
    }

    override fun onSetPassword(code: String, password: String) {
        authRepository.recoverPasswordNew(RecoverPasswordBody("phone", code, password))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({}, {  }).call(compositeDisposable)
    }

    override fun onCloseClick() {
        viewState.navigateUp()
    }
}
