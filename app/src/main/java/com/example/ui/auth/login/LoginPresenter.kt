package com.example.ui.auth.login

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.ApiError
import com.example.data.models.SnUser
import com.example.repository.AuthRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.snAuth.SnAuthManager
import com.example.util.AuthValidateUtil
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.reactivex.rxkotlin.plusAssign
import isValidPhoneNumber
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class LoginPresenter
@Inject constructor(
        private val authRepository: AuthRepository,
        private val phoneNumberUtil: PhoneNumberUtil,
        snAuthManager: SnAuthManager
) : BaseAuthPresenter<LoginContract.View>(authRepository, snAuthManager), LoginContract.Presenter {

    companion object {
        private const val WRONG_PASSWORD_API_ERROR = "combination email and password not found"
    }

    var login = ""
    var password = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.apply {
            setLoginAndPassword(login, password)
        }
    }

    override fun onClickBack() = viewState.navigateUp()

    override fun onChangeLoginText(login: String) {
        this.login = login
        viewState.showLoginError(false)
        performDataChange()
    }

    override fun onChangePasswordText(password: String) {
        this.password = password
        viewState.showPasswordError(false)
        performDataChange()
    }

    override fun onClickRecoverPassword() {
        val email = login.let { if (AuthValidateUtil.isValidEmail(it)) it else "" }
        viewState.showRecoveryPassword(email)
    }

    override fun onClickLogin(login: String, password: String) {
        compositeDisposable += authRepository.authEmailOrPhone(login, password)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onError = {
                            if ((it as? ApiError)?.hasError(WRONG_PASSWORD_API_ERROR) == true) {
                                viewState.showWrongPasswordError()
                            } else {
                                onReceiveError(it)
                            }
                        },
                        onComplete = {
                            // do nothing
                        }
                )
    }

    private fun performDataChange() {
        viewState.enableLoginBtn(isDataValid())
    }

    private fun isDataValid(): Boolean {
        return (AuthValidateUtil.isValidEmail(login) || login.isValidPhoneNumber())
                && password.isNotEmpty()
    }

    override fun onContinueWithSnRegistration(snUser: SnUser) {
        viewState.showSnRegistration(snUser)
    }

    private fun String.isValidPhoneNumber(): Boolean {
        return isValidPhoneNumber(phoneNumberUtil)
    }
}
