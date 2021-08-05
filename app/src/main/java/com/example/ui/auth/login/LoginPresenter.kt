package com.example.ui.auth.login

import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.AuthBody
import com.example.data.bodies.LoginModel
import com.example.data.bodies.RebaseInviteBody
import com.example.data.models.ApiError
import com.example.data.models.SnUser
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.snAuth.SnAuthManager
import com.example.util.AuthValidateUtil
import com.example.util.Utils.isContainLetters
import com.example.util.Utils.isPhone
import com.example.util.Utils.newPhoneValidator
import com.example.util.Utils.validatePhoneBeforeSend
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
        private val userRepository: UserRepository,
        private val appData: AppData,
        snAuthManager: SnAuthManager
) : BaseAuthPresenter<LoginContract.View>(authRepository, snAuthManager), LoginContract.Presenter {

    companion object {
        private const val WRONG_PASSWORD_API_ERROR = "combination email and password not found"
        private const val WRONG_EMAIL_API_ERROR = "combination user and username not found"
    }

    var login = ""
    var password = ""
    var loginType = "email"

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.apply {
            setLoginAndPassword(login, password)
        }
    }

    override fun onClickBack() = viewState.navigateUp()

    override fun onChangeLoginText(login: String, context: Context) {
        this.login = login
        viewState.showLoginError(false)
        performDataChange(context)
    }

    override fun onChangePasswordText(password: String, context: Context) {
        this.password = password
        viewState.showPasswordError(false)
        performDataChange(context)
    }

    override fun onClickRecoverPassword() {
        val email = login.let { if (AuthValidateUtil.isValidEmail(it)) it else "" }
        viewState.showRecoveryPassword(email)
    }

    override fun onClickLogin(login: String, password: String, invite: Int) {
        val validatedLogin = if (loginType == "phone") validatePhoneBeforeSend(login) else login
        compositeDisposable += authRepository.authEmailOrPhoneWithResult(AuthBody(LoginModel(loginType, validatedLogin), LoginModel("common", password)))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onError = {
                            val hasApiError = (it as? ApiError)
                                    ?.hasError(WRONG_PASSWORD_API_ERROR, WRONG_EMAIL_API_ERROR)

                            if (hasApiError == true) {
                                viewState.showWrongPasswordError()
                            } else {
                                onReceiveError(it)
                            }
                        },
                        onSuccess = {
                            if (invite != -1)
                                compositeDisposable += authRepository.rebaseInvite(invite, RebaseInviteBody(it.id?: 0, it.token?: ""))
                                        .withCheckInternetConnectivity()
                                        .performOnBackgroundOutOnMain()
                                        .subscribeSimple(
                                                onError = {},
                                                onComplete = {}
                                        )
                            // do nothing
                        }
                )
    }

    private fun performDataChange(context: Context) {
        viewState.enableLoginBtn(isDataValid(context))
    }

    private fun isDataValid(context: Context): Boolean {
        /*return (AuthValidateUtil.isValidEmail(login) || login.isValidPhoneNumber())
                && password.isNotEmpty()*/
        return if (isPhone(login) && !isContainLetters(login)) {
            loginType = "phone"
            newPhoneValidator(context, login) && password.isNotEmpty()
        } else {
            loginType = "email"
            AuthValidateUtil.isValidEmail(login) && password.isNotEmpty()
        }
    }

    override fun onContinueWithSnRegistration(snUser: SnUser) {
        viewState.showSnRegistration(snUser)
    }

    private fun String.isValidPhoneNumber(): Boolean {
        return isValidPhoneNumber(phoneNumberUtil)
    }
}
