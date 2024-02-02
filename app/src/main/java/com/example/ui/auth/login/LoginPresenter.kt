package com.example.ui.auth.login

import android.util.Log
import com.example.data.AppData
import com.example.data.bodies.AuthBody
import com.example.data.bodies.LoginModel
import com.example.data.bodies.RebaseInviteBody
import com.example.data.models.ApiError
import com.example.data.models.AuthResponse
import com.example.data.models.SnAuth
import com.example.extensions.getAppVersion
import com.example.extensions.getAppVersionCode
import com.example.extensions.getDeviceName
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.util.AuthValidateUtil
import com.example.util.Utils.isContainLetters
import com.example.util.Utils.isPhone
import com.example.util.Utils.isPhoneNumberValid
import com.example.util.Utils.validatePhoneBeforeSend
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withInfinityCustomLoading
import javax.inject.Inject

@InjectViewState
class LoginPresenter
@Inject constructor(
    private val authRepository: AuthRepository,
    private val appData: AppData,
) : BasePresenter<LoginContract.View>(appData), LoginContract.Presenter {

    var login = ""
    var snAuth : SnAuth? = null
    private var password = ""
    private var loginType = "email"

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setLoginAndPassword(login, password)
    }

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
            if (invite != -1) authRepository.authEmailOrPhoneWithInvite(invite, getLoginBody())
            else if (snAuth != null) authRepository.authEmailOrPhoneWithSn(getLoginBody(), snAuth!!)
            else authRepository.authEmailOrPhone(getLoginBody())
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


    private fun getLoginBody(): AuthBody {
        val validatedLogin = if (loginType == "phone") validatePhoneBeforeSend(login) else login
        return AuthBody(
            LoginModel(loginType, validatedLogin),
            LoginModel("common", password),
            appData.deviceId ?: "",
            getDeviceName(),
            getAppVersionCode(),
            getAppVersion()
        )
    }

    private fun catchError(it: Throwable) {
        try {
            it.printStackTrace()
            val apiError = (it as? ApiError)
            if (apiError == null) viewState.showWrongPasswordError()
            else if (apiError.hasError(TOO_MANY_ATTEMPTS_ERROR)) viewState.showAccountBlockingDialog()
            else viewState.showWrongPasswordError()

        } catch (_: Exception) { }
    }

    companion object {
        private const val TOO_MANY_ATTEMPTS_ERROR = "Too many login attempts"
        private const val WRONG_PASSWORD_ERROR = "Incorrect password"
        private const val WRONG_PASSWORD_API_ERROR = "combination email and password not found"
        private const val WRONG_EMAIL_API_ERROR = "combination user and username not found"
    }
}
