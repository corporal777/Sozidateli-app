package com.example.ui.userprofile.common.password.reset

import android.app.NotificationManager
import com.example.data.AppData
import com.example.data.bodies.PasswordBody
import com.example.data.bodies.RecoverPasswordBody
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCustomLoading
import withInfinityCustomLoading
import javax.inject.Inject

@InjectViewState
class ResetPasswordPresenter
@Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val appData: AppData,
) : BasePresenter<ResetPasswordContract.View>(appData), ResetPasswordContract.Presenter {

    var recoverCode = ""
    var userId = ""
    var loginType = ""

    private var password = ""
    private var passwordIsValid = false


    override fun attachView(view: ResetPasswordContract.View?) {
        super.attachView(view)
        viewState.enableBtnReset(isPasswordValid())
    }

    override fun onChangePasswordText(password: String?, isValid: Boolean) {
        this.password = password ?: ""
        this.passwordIsValid = isValid
        viewState.enableBtnReset(isPasswordValid())
    }

    override fun onRecoveryPasswordClick() {
        if (!isPasswordValid()) return
        compositeDisposable += authRepository.recoverPassword(RecoverPasswordBody(loginType, recoverCode, password, userId))
            .performOnBackgroundOutOnMain()
            .withInfinityCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = {}
            )
    }

    override fun onSavePasswordClick() {
        if (!isPasswordValid()) return
        compositeDisposable += userRepository.changePassword(PasswordBody(password))
            .doOnComplete { appData.getUser().state?.isEmptyPassword = false }
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = { viewState.showPasswordSuccessChanged() }
            )
    }

    private fun isPasswordValid() = !password.isNullOrBlank() && passwordIsValid
    fun isChangePassword() = recoverCode.isBlank() && userId.isBlank() && loginType.isBlank()
}