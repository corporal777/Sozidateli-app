package com.example.ui.auth.login

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
import com.shakebugs.shake.Shake
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.reactivex.rxkotlin.plusAssign
import isValidPhoneNumber
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class LoginPresenter
@Inject constructor(
    private val authRepository: AuthRepository,
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
    var deviceId = appData.deviceId
    var deviceModel = ""
    var appVersion = getAppVersion()
    var appCode = getAppVersionCode()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setLoginAndPassword(login, password)
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
        val validatedLogin = if (loginType == "phone") validatePhoneBeforeSend(login) else login
        viewState.showCustomProgressDialog()
        if (invite != -1) {
            compositeDisposable += authRepository.authEmailOrPhoneWithResult(getLoginBody(validatedLogin))
                .flatMapCompletable { authRepository.rebaseInvite(invite, RebaseInviteBody(it.id ?: 0, it.token ?: "")) }
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .subscribeSimple(
                    onError = {
                        it.printStackTrace()
                        val hasApiError = (it as? ApiError)?.hasError(WRONG_PASSWORD_API_ERROR, WRONG_EMAIL_API_ERROR)
                        viewState.apply {
                            hideCustomProgressDialog()
                            if (hasApiError == true) showWrongPasswordError()
                            else onReceiveError(it)
                        }
                    },
                    onComplete = { Shake.registerUser(appData.getId().toString()) }
                )
        } else {
            compositeDisposable += authRepository.authEmailOrPhone(getLoginBody(validatedLogin))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .subscribeSimple(
                    onError = {
                        it.printStackTrace()
                        val hasApiError = (it as? ApiError)?.hasError(WRONG_PASSWORD_API_ERROR, WRONG_EMAIL_API_ERROR)
                        viewState.apply {
                            hideCustomProgressDialog()
                            if (hasApiError == true) showWrongPasswordError()
                            else onReceiveError(it)
                        }
                    },
                    onComplete = { Shake.registerUser(appData.getId().toString()) }
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

    private fun getLoginBody(login: String): AuthBody {
        return AuthBody(
            LoginModel(loginType, login),
            LoginModel("common", password),
            deviceId?:"",
            deviceModel,
            appCode,
            appVersion
        )
    }
}
