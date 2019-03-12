package com.example.ui.auth.loginEmail

import call
import com.arellomobile.mvp.InjectViewState
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.util.AuthUtil
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class LoginEmailPresenter
@Inject constructor(private val authRepository: AuthRepository
) : BasePresenter<LoginEmailContract.View>(), LoginEmailContract.Presenter {

    var email = ""
    var password = ""
    var showConfirmationOnStart = false
    var showRecoveryOnStart = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.apply {
            setEmailAndPassword(email, password)
            validateEmail()
            if (showConfirmationOnStart) showEmailConfirmDialog(email)
            if (showRecoveryOnStart) showEmailRecoveryDialog(email)
        }
    }

    override fun onClickBack() = viewState.navigateUp()

    override fun onChangeEmailText(email: String) {
        this.email = email
        validateEmail()
    }

    override fun onChangePasswordText(password: String) {
        this.password = password
    }

    private fun validateEmail() {
        viewState.enableLoginBtn(AuthUtil.isValidEmail(email))
    }

    override fun onClickRecoverPassword() {
        viewState.showRecoveryPassword(email)
    }

    override fun onClickLogin() {
        authRepository.authEmail(email, password)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                }, { })
                .call(compositeDisposable)
    }

    override fun onClickRegister() = viewState.showRegister()
}
