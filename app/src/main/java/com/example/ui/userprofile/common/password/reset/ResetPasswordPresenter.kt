package com.example.ui.userprofile.common.password.reset

import android.app.NotificationManager
import com.example.data.AppData
import com.example.data.bodies.PasswordBody
import com.example.data.bodies.RecoverPasswordBody
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
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
    private val appData: AppData,
) : BasePresenter<ResetPasswordContract.View>(appData), ResetPasswordContract.Presenter {

    var recoverCode = ""
    var userId = ""
    var loginType = ""
    private var password = ""
    private var passwordIsValid = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.enableBtnResetPassword(isPasswordValid())
    }

    override fun onChangePasswordText(password: String?, isValid: Boolean) {
        this.password = password ?: ""
        this.passwordIsValid = isValid
        viewState.enableBtnResetPassword(isPasswordValid())
    }

    override fun onRecoveryPasswordClick() {
        if (isPasswordValid()) {
            compositeDisposable += authRepository.recoverPassword(
                RecoverPasswordBody(loginType, recoverCode, password, userId)
            )
                .performOnBackgroundOutOnMain()
                .withInfinityCustomLoading(viewState)
                .subscribeSimple(
                    onError = { onReceiveError(it) },
                    onComplete = {}
                )
        }
    }

    private fun isPasswordValid() = !password.isNullOrBlank() && passwordIsValid

}