package com.example.ui.auth.authorization

import com.example.data.AppData
import com.example.data.models.SnUser
import com.example.repository.AuthRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.snAuth.SnAuthManager
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class AuthorizationPresenter
@Inject constructor(
        authRepository: AuthRepository,
        private val snAuthManager: SnAuthManager,
        appData: AppData
) : BaseAuthPresenter<AuthorizationContract.View>(authRepository, snAuthManager, appData), AuthorizationContract.Presenter {

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
