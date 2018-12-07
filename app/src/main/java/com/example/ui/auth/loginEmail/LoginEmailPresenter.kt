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
    private var isPasswordValid = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.apply {
            enableLoginBtn(false)
        }
    }


    override fun clickOnBack() = viewState.navigateUp()

    override fun changeEmailText(email: String) {
        isEmailValid = AuthUtil.isValidEmail(email)
        viewState.enableLoginBtn(isEmailValid && isPasswordValid)
    }

    override fun chagnePasswordText(password: String) {
        isPasswordValid = AuthUtil.isValidPassword(password)
        viewState.enableLoginBtn(isEmailValid && isPasswordValid)
    }

    override fun clickLogin() {
        authRepository.authEmail()
                .performOnBackgroundOutOnMain()
                .subscribe {
                    viewState.showWelcome()
                }.call(compositeDisposable)
    }

    override fun clickRegister() = viewState.showRegister()
}
