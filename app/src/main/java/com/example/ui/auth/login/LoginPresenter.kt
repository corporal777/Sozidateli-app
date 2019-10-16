package com.example.ui.auth.login

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.SnUser
import com.example.repository.AuthRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.snAuth.SnAuthManager
import com.example.util.AuthValidateUtil
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class LoginPresenter
@Inject constructor(
        private val authRepository: AuthRepository,
        snAuthManager: SnAuthManager
) : BaseAuthPresenter<LoginContract.View>(authRepository, snAuthManager), LoginContract.Presenter {

    var email = ""
    var password = ""

    private var snUserToRegister: SnUser? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.apply {
            setEmailAndPassword(email, password)
        }
    }

    override fun onClickBack() = viewState.navigateUp()

    override fun onChangeEmailText(email: String) {
        this.email = email
        viewState.showEmailError(false)
        performDataChange()
    }

    override fun onChangePasswordText(password: String) {
        this.password = password
        viewState.showPasswordError(false)
        performDataChange()
    }

    override fun onClickRecoverPassword() {
        viewState.showRecoveryPassword(email)
    }

    override fun onClickLogin(email: String, password: String) {
        compositeDisposable += authRepository.authEmail(email, password)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { }
    }

    private fun performDataChange() {
        viewState.enableLoginBtn(isDataValid())
    }

    private fun isDataValid(): Boolean {
        return AuthValidateUtil.isValidEmail(email)
                && password.isNotEmpty()
    }

    override fun onContinueRegistration(snUser: SnUser) {
        snUserToRegister = snUser
        viewState.showRegistrationConfirmation(true)
    }

    override fun onRegistrationCancel() {
        snUserToRegister = null
        viewState.showRegistrationConfirmation(false)
    }

    override fun onRegistrationConfirm() {
        viewState.showRegistrationConfirmation(false)
        viewState.showRegistration(snUserToRegister)
    }
}
