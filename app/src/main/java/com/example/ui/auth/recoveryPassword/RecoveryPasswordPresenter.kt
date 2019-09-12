package com.example.ui.auth.recoveryPassword

import com.arellomobile.mvp.InjectViewState
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.util.AuthValidateUtil
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class RecoveryPasswordPresenter
@Inject constructor(
        private val authRepository: AuthRepository
) : BasePresenter<RecoveryPasswordContract.View>(), RecoveryPasswordContract.Presenter {

    var email = ""

    private var onUserUnderstandEverything = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.apply {
            setEmail(email)
        }
    }

    override fun onRecoveryClick() {
        if (isDataValid()) {
            compositeDisposable += authRepository.sendRecoveryEmail(email)
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribe({
                        viewState.showRecoveryNotification(email)
                    }, {
                        it.printStackTrace()
                        viewState.showToast(it.message ?: it.localizedMessage)
                    })
        } else {
            viewState.showEmailError(true)
        }
    }

    override fun onChangeEmailText(email: String) {
        viewState.showEmailError(false)
        this.email = email
        performDataChange()
    }

    private fun performDataChange() {
        viewState.enableRecoveryBtn(isDataValid())
    }

    private fun isDataValid(): Boolean {
        return AuthValidateUtil.isValidEmail(email)
    }

    override fun onUserUnderstand() {
        if (!onUserUnderstandEverything) {
            onUserUnderstandEverything = true
            viewState.navigateUp()
        }
    }

    override fun onCloseClick() {
        viewState.navigateUp()
    }
}
