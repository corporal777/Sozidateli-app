package com.example.ui.auth.login

import call
import com.arellomobile.mvp.InjectViewState
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.ui.snAuth.SnAuth
import com.example.ui.snAuth.SnAuthError
import com.example.ui.snAuth.SnAuthManager
import com.example.ui.snAuth.SnType
import io.reactivex.Completable
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class LoginPresenter
@Inject constructor(
        private val authRepository: AuthRepository
) : BasePresenter<LoginContract.View>(), LoginContract.Presenter {

    private val snAuthListener = object : SnAuthManager.OnSnAuthListener {
        override fun onSnAuthComplete(snAuth: SnAuth) {
            when (snAuth.snType) {
                SnType.VK -> executeAuthorization(authRepository.authVk(snAuth.token, snAuth.email))
                SnType.FB -> executeAuthorization(authRepository.authFb(snAuth.token))
                SnType.OK -> executeAuthorization(authRepository.authOk(snAuth.token))
            }

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

    private fun executeAuthorization(completable: Completable) {
        completable.performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                }, { viewState.showToast(it.message ?: "") })
                .call(compositeDisposable)
    }

    private fun setSnAuthListener() {
        SnAuthManager.addOnSnAuthListener(snAuthListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        SnAuthManager.removeOnSnAuthListener(snAuthListener)
    }
}
