package com.example.ui.auth.loginEmail

import call
import com.arellomobile.mvp.InjectViewState
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.util.AuthUtil
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class LoginEmailPresenter
@Inject constructor(private val authRepository: AuthRepository
) : BasePresenter<LoginEmailContract.View>(), LoginEmailContract.Presenter {

    private var isEmailValid = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.apply { enableLoginBtn(false) }
    }

    override fun onClickBack() = viewState.navigateUp()

    override fun onChangeEmailText(email: String) {
        isEmailValid = AuthUtil.isValidEmail(email)
        viewState.enableLoginBtn(isEmailValid)
    }

    override fun onChangePasswordText(password: String) {

    }

    override fun onClickLogin(email: String, password: String) {
        authRepository.authEmail(email, password)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.showWelcome()
                }, {
                    viewState.showToast(it.message ?: it.localizedMessage)
                })
                .call(compositeDisposable)
    }

    override fun onClickRegister() = viewState.showRegister()
}
