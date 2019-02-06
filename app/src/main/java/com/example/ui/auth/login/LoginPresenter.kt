package com.example.ui.auth.login

import call
import com.arellomobile.mvp.InjectViewState
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class LoginPresenter
@Inject constructor(
        private val authRepository: AuthRepository
) : BasePresenter<LoginContract.View>(), LoginContract.Presenter {

    override fun onClickVk() {
        authRepository.authSN()
                .performOnBackgroundOutOnMain()
                .subscribe {
                    viewState.showWelcome()
                }
                .call(compositeDisposable)
    }

    override fun onClickFb() {
    }

    override fun onClickOk() {
    }

    override fun onClickEmail() {
        viewState.showLogin()
    }
}
