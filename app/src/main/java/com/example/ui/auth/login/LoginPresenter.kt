package com.example.ui.auth.login

import com.example.data.AppData
import com.example.data.bodies.AuthBody
import com.example.data.bodies.LoginModel
import com.example.data.bodies.RebaseInviteBody
import com.example.data.models.ApiError
import com.example.data.models.NewAuthResponse
import com.example.extensions.getAppVersion
import com.example.extensions.getAppVersionCode
import com.example.extensions.getDeviceName
import com.example.repository.AuthRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.snAuth.SnAuth
import com.example.ui.snAuth.SnAuthManager
import com.example.util.AuthValidateUtil
import com.example.util.Utils.isContainLetters
import com.example.util.Utils.isPhone
import com.example.util.Utils.isPhoneNumberValid
import com.example.util.Utils.validatePhoneBeforeSend
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withInfinityCustomLoading
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
    private var password = ""
    private var loginType = "email"
    private var deviceId = appData.deviceId
    private var deviceModel = getDeviceName()
    private var appVersion = getAppVersion()
    private var appCode = getAppVersionCode()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setLoginAndPassword(login, password)
    }

    override fun onClickBack() = viewState.navigateUp()

    override fun onChangeLoginText(value: String) {
        this.login = value
        viewState.showLoginError(false)
        performDataChange()
    }

    override fun onChangePasswordText(value: String) {
        this.password = value
        viewState.showPasswordError(false)
        performDataChange()
    }

    override fun onClickRecoverPassword() {
        val email = login.let { if (AuthValidateUtil.isValidEmail(it)) it else "" }
        viewState.showRecoveryPassword(email)
    }

    override fun onClickLogin(invite: Int) {
        compositeDisposable += Completable.defer {
            val validatedLogin = if (loginType == "phone") validatePhoneBeforeSend(login) else login
            if (invite != -1) {
                authRepository.authEmailOrPhoneWithResult(getLoginBody(validatedLogin))
                    .flatMapCompletable { authRepository.rebaseInvite(invite, getInviteBody(it)) }
            } else authRepository.authEmailOrPhone(getLoginBody(validatedLogin))
        }
            .performOnBackgroundOutOnMain()
            .withInfinityCustomLoading(viewState)
            .subscribeSimple(
                onError = { catchError(it) },
                onComplete = {})
    }

    private fun performDataChange() {
        viewState.enableLoginBtn(isDataValid())
    }

    private fun isDataValid(): Boolean {
        return if (isPhone(login) && !isContainLetters(login)) {
            loginType = "phone"
            isPhoneNumberValid(login) && password.isNotEmpty()
        } else {
            loginType = "email"
            AuthValidateUtil.isValidEmail(login) && password.isNotEmpty()
        }
    }

    override fun onContinueWithSnRegistration(SnAuth: SnAuth) {
        //viewState.showSnRegistration(snUser)
    }

    private fun getLoginBody(login: String): AuthBody {
        return AuthBody(
            LoginModel(loginType, login),
            LoginModel("common", password),
            deviceId ?: "",
            deviceModel,
            appCode,
            appVersion
        )
    }

    private fun getInviteBody(auth: NewAuthResponse): RebaseInviteBody {
        return RebaseInviteBody(auth.id ?: 0, auth.token ?: "")
    }

    private fun catchError(it: Throwable) {
        it.printStackTrace()
        val hasApiError = (it as? ApiError)?.hasError(WRONG_PASSWORD_API_ERROR, WRONG_EMAIL_API_ERROR)
        if (hasApiError == true) viewState.showWrongPasswordError()
        else onReceiveError(it)
    }
}
