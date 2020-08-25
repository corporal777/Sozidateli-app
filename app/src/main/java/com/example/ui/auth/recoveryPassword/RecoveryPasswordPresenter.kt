package com.example.ui.auth.recoveryPassword

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.ApiError
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.util.AuthValidateUtil
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class RecoveryPasswordPresenter
@Inject constructor(
        private val authRepository: AuthRepository
) : BasePresenter<RecoveryPasswordContract.View>(), RecoveryPasswordContract.Presenter {

    companion object {
        private const val USER_NOT_REGISTERED_ERROR = "User is not registered yet"
    }

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
                    .withCheckInternetConnectivity()
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple(
                            onError = {
                                if ((it as? ApiError)?.hasError(USER_NOT_REGISTERED_ERROR) == true) {
                                    viewState.showWrongEmailError()
                                } else {
                                    onReceiveError(it)
                                }
                            },
                            onComplete = {
                                viewState.showRecoveryNotification(email)
                            }
                    )
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
