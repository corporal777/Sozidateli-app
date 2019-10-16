package com.example.ui.auth.base

import com.example.data.models.SnUser
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.ui.snAuth.SnAuth
import com.example.ui.snAuth.SnAuthError
import com.example.ui.snAuth.SnAuthManager
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain

abstract class BaseAuthPresenter<V : BaseAuthContract.View>
constructor(
        private val authRepository: AuthRepository,
        private val snAuthManager: SnAuthManager
) : BasePresenter<V>(), BaseAuthContract.Presenter {

    private val snAuthListener = object : SnAuthManager.OnSnAuthListener {
        override fun onSnAuthComplete(snAuth: SnAuth) {
            checkSnRegistration(snAuth)
        }

        override fun onSnAuthError(error: SnAuthError) {
            error.message?.let { errorMessage -> viewState.showToast(errorMessage) }
        }
    }

    private fun checkSnRegistration(snAuth: SnAuth) {
        viewState.showLoadingDialog()
        compositeDisposable += authRepository.authSocialNetwork(snAuth)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val snUser = it.second
                    val status = it.first
                    if (!status.social_auth_found || !status.user_by_social_confirmed_email) {
                        viewState.hideLoadingDialog()
                        onContinueRegistration(snUser)
                    } else {
                        authorize(snUser.snAuth)
                    }
                }, {
                    viewState.hideLoadingDialog()
                    it.printStackTrace()
                })
    }

    private fun authorize(snAuth: SnAuth) {
        compositeDisposable += authRepository.authSocialNetwork(snAuth.snType.code, snAuth.token)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.hideLoadingDialog()
                }, {
                    viewState.hideLoadingDialog()
                    it.printStackTrace()
                })
    }

    override fun authVk() {
        compositeDisposable += checkInternetAndRun {
            snAuthManager.apply {
                addOnSnAuthListener(snAuthListener)
                startAuthVk()
            }
        }
    }

    override fun authFb() {
        compositeDisposable += checkInternetAndRun {
            snAuthManager.apply {
                addOnSnAuthListener(snAuthListener)
                startAuthFacebook()
            }
        }
    }

    override fun authOk() {
        compositeDisposable += checkInternetAndRun {
            snAuthManager.apply {
                addOnSnAuthListener(snAuthListener)
                startAuthOk()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        snAuthManager.removeOnSnAuthListener(snAuthListener)
    }

    abstract fun onContinueRegistration(snUser: SnUser)
}
