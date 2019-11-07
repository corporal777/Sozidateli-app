package com.example.ui.auth.authorization

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.SnUser
import com.example.repository.AuthRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.snAuth.SnAuthManager
import javax.inject.Inject

@InjectViewState
class AuthorizationPresenter
@Inject constructor(
        authRepository: AuthRepository,
        private val snAuthManager: SnAuthManager
) : BaseAuthPresenter<AuthorizationContract.View>(authRepository, snAuthManager), AuthorizationContract.Presenter {

    override fun onLoginClick() {
        snAuthManager.removeOnSnAuthListener(snAuthListener)
        viewState.showLogin()
    }

    override fun onEmailClick() {
        snAuthManager.removeOnSnAuthListener(snAuthListener)
        viewState.showEmailRegistration()
    }

    override fun onContinueWithSnRegistration(snUser: SnUser) {
        viewState.showSnRegistration(snUser)
    }
}
