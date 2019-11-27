package com.example.ui.auth.base

import com.example.data.models.SnUser
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.ui.snAuth.SnAuth
import com.example.ui.snAuth.SnAuthError
import com.example.ui.snAuth.SnAuthManager
import com.vk.sdk.VKScope
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain

abstract class BaseAuthPresenter<V : BaseAuthContract.View>
constructor(
        private val authRepository: AuthRepository,
        private val snAuthManager: SnAuthManager
) : BasePresenter<V>(), BaseAuthContract.Presenter {

    protected val snAuthListener = object : SnAuthManager.OnSnAuthListener {
        override fun onSnAuthComplete(snAuth: SnAuth) {
            checkSnRegistration(snAuth)
        }

        override fun onSnAuthError(error: SnAuthError) {
            error.message?.let { errorMessage -> viewState.showRequestErrorMessage() }
        }
    }

    private fun checkSnRegistration(snAuth: SnAuth) {
        viewState.showLoadingDialog()
        compositeDisposable += authRepository.authSocialNetwork(snAuth)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val snUser = it.second
                    val status = it.first

                    if (status.social_auth_found && status.user_by_social_confirmed_email) {
                        authorize(snUser)
                    } else {
                        viewState.hideLoadingDialog()
                        onContinueWithSnRegistration(snUser)
                    }
                }, {
                    viewState.hideLoadingDialog()
                    it.printStackTrace()
                })
    }

    private fun authorize(snUser: SnUser) {
        val snAuth = snUser.snAuth
        compositeDisposable += authRepository.authSocialNetwork(snAuth.snType.code, snAuth.token)
                .performOnBackgroundOutOnMain()
                .subscribeSimple(onApiError = {
                    viewState.hideLoadingDialog()
                    if (it.hasError(ERROR_NEED_REGISTRATION)) onContinueWithSnRegistration(snUser)
                }, onComplete = {
                    viewState.hideLoadingDialog()
                })
    }

    override fun authVk() {
        compositeDisposable += checkInternetAndRun {
            snAuthManager.apply {
                addOnSnAuthListener(snAuthListener)
                startAuthVk(arrayOf(VKScope.EMAIL))
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

    abstract fun onContinueWithSnRegistration(snUser: SnUser)

    companion object {
        private const val ERROR_NEED_REGISTRATION = "NEED_REGISTRATION"
    }
}
