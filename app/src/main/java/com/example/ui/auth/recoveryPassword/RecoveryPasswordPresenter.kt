package com.example.ui.auth.recoveryPassword

import call
import com.arellomobile.mvp.InjectViewState
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.util.AuthUtil
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class RecoveryPasswordPresenter
@Inject constructor(
        private val authRepository: AuthRepository
) : BasePresenter<RecoveryPasswordContract.View>(), RecoveryPasswordContract.Presenter {

    var email = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.apply {
            validateEmail()
            setEmail(email)
        }
    }

    override fun onRecoveryClick() {
        authRepository.sendRecoveryEmail(email)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.goToLoginWithEmailRecovery(email)
                }, {
                })
                .call(compositeDisposable)
    }

    private fun validateEmail() {
        viewState.enableRecoveryBtn(AuthUtil.isValidEmail(email))
    }

    override fun onChangeEmailText(email: String) {
        this.email = email
        validateEmail()
    }
}
