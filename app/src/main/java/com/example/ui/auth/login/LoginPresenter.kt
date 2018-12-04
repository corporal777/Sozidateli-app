package com.example.ui.auth.login

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.UserChat
import com.example.repository.AuthRepository
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class LoginPresenter
@Inject constructor(private val authRepository: AuthRepository) : BasePresenter<LoginContract.View>(), LoginContract.Presenter{


    override fun clickOnVkAuth() {
        authRepository.authSN()
                .performOnBackgroundOutOnMain()
                .subscribe {
                    viewState.showWelcome()
                }
                .call(compositeDisposable)
    }

    override fun clickOnFbAuth() {
        authRepository.authSN()
                .performOnBackgroundOutOnMain()
                .subscribe {
                    viewState.showWelcome()
                }
                .call(compositeDisposable)
    }

    override fun clickOnLoginEmail() {
        viewState.showLogin()
    }
}
