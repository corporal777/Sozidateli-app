package com.example.ui.auth.login

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.AuthResponse
import com.example.data.models.AuthSNResponse
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.ui.snAuth.SnAuth
import com.example.ui.snAuth.SnAuthError
import com.example.ui.snAuth.SnAuthManager
import com.example.ui.snAuth.SnType
import com.example.util.SN_FB
import com.example.util.SN_OK
import com.example.util.SN_VK
import io.reactivex.Completable
import io.reactivex.Single
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
            val sn = snAuth.snType.code
            executeAuthorization(snAuth.snType, authRepository.authSocialNetwork(sn, snAuth.token,snAuth.email), snAuth.email,snAuth.token)
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

    override fun onClickSetSocialNetworkEmail(snType: SnType, email: String,token:String) {
        authRepository.setEmailSocialNetwork(snType.code, email,token)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.showNeedConfirmEmailDialog(email)
                }, {
                    it.printStackTrace()
                    viewState.showSocialNetworkSetEmail(snType, email,token)
                }).call(compositeDisposable)
    }

    private fun executeAuthorization(snType: SnType, request: Single<AuthSNResponse>, email: String? = null,token: String) {
        request.performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    if (it.user_email_not_set) {
                        viewState.showSocialNetworkSetEmail(snType, email,token)
                    } else {
                        if (!it.user_email_confirmed) {
                            viewState.showNeedConfirmEmailDialog(null)
                        }
                    }
                }, { it.printStackTrace() })
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
