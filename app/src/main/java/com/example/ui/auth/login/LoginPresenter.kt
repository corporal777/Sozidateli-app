package com.example.ui.auth.login

import android.content.Context
import android.util.Log
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
import com.example.util.getAppVersion
import com.example.util.getAppVersionCode
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
) : BaseAuthPresenter<LoginContract.View>(authRepository, snAuthManager, appData),
    LoginContract.Presenter {

    companion object {
        private const val WRONG_PASSWORD_API_ERROR = "combination email and password not found"
        private const val WRONG_EMAIL_API_ERROR = "combination user and username not found"
    }

    var login = ""
    var password = ""
    var loginType = "email"
    var deviceId = ""
    var deviceModel = ""
    var appVersion = getAppVersion()
    var appCode = getAppVersionCode()

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

    override fun onClickLogin(login: String, password: String, invite: Int) {
        viewState.showCustomProgressDialog()
        Log.e("PPP", appVersion)
        val validatedLogin = if (loginType == "phone") validatePhoneBeforeSend(login) else login
        if (invite != -1) {
            compositeDisposable += authRepository.authEmailOrPhoneWithResult(
                AuthBody(
                    LoginModel(loginType, validatedLogin),
                    LoginModel("common", password),
                    deviceId,
                    deviceModel,
                    appCode,
                    appVersion
                )
            )
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                //.withLoadingDialog(viewState)
                .subscribeSimple(
                    onError = {
                        viewState.hideCustomProgressDialog()
                        it.printStackTrace()
                        val hasApiError = (it as? ApiError)
                            ?.hasError(WRONG_PASSWORD_API_ERROR, WRONG_EMAIL_API_ERROR)

                        if (hasApiError == true) {
                            viewState.showWrongPasswordError()
                        } else {
                            onReceiveError(it)
                        }
                    },
                    onSuccess = {
                        compositeDisposable += authRepository.rebaseInvite(
                            invite, RebaseInviteBody(
                                it.id
                                    ?: 0, it.token ?: ""
                            )
                        )
                            .withCheckInternetConnectivity()
                            .performOnBackgroundOutOnMain()
                            .subscribeSimple(
                                onError = {},
                                onComplete = {}
                            )
                        // do nothing
                    }
                )
        } else {
            compositeDisposable += authRepository.authEmailOrPhone(
                AuthBody(
                    LoginModel(loginType, validatedLogin),
                    LoginModel("common", password),
                    deviceId,
                    deviceModel,
                    appCode,
                    appVersion
                )
            )
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                //.withLoadingDialog(viewState)
                .subscribeSimple(
                    onError = {
                        viewState.hideCustomProgressDialog()
                        it.printStackTrace()
                        val hasApiError = (it as? ApiError)
                            ?.hasError(WRONG_PASSWORD_API_ERROR, WRONG_EMAIL_API_ERROR)

                        if (hasApiError == true) {
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
    }

    private fun performDataChange() {
        viewState.enableLoginBtn(isDataValid())
    }

    private fun isDataValid(): Boolean {
        return if (isPhone(login) && !isContainLetters(login)) {
            loginType = "phone"
            newPhoneValidator(login) && password.isNotEmpty()
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
