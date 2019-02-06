package com.example.ui.auth.login

import com.arellomobile.mvp.InjectViewState
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.ui.snAuth.SnAuth
import com.example.ui.snAuth.SnAuthError
import com.example.ui.snAuth.SnAuthManager
import io.reactivex.Single
import javax.inject.Inject

@InjectViewState
class LoginPresenter
@Inject constructor(
        private val authRepository: AuthRepository
) : BasePresenter<LoginContract.View>(), LoginContract.Presenter {

    private val snAuthListener = object : SnAuthManager.OnSnAuthListener {
        override fun onSnAuthComplete(snAuth: SnAuth) {
            authRepository.authSN()
        }

        override fun onSnAuthError(error: SnAuthError) {
            error.message?.let { errorMessage -> viewState.showToast(errorMessage) }
        }
    }

    override fun onClickVk() {
        setSnAuthListener()
        viewState.startVkAuthorization()
    }

    override fun onClickFb() {
        setSnAuthListener()
        viewState.startFbAuthorization()
    }

    override fun onClickOk() {
        setSnAuthListener()
        viewState.startOkAuthorization()
    }

    override fun onClickEmail() {
        viewState.showLogin()
    }

    private fun setSnAuthListener() {
        SnAuthManager.addOnSnAuthListener(snAuthListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        SnAuthManager.removeOnSnAuthListener(snAuthListener)
    }
}
