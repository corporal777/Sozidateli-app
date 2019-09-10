package com.example.ui.auth.authorization

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.RegisterStatus
import com.example.data.models.SnUser
import com.example.data.models.SnUserData
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.ui.snAuth.SnAuth
import com.example.ui.snAuth.SnAuthError
import com.example.ui.snAuth.SnAuthManager
import com.example.ui.snAuth.SnType
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class AuthorizationPresenter
@Inject constructor(
        private val authRepository: AuthRepository
) : BasePresenter<AuthorizationContract.View>(), AuthorizationContract.Presenter {

    private val snAuthListener = object : SnAuthManager.OnSnAuthListener {
        override fun onSnAuthComplete(snAuth: SnAuth) {
            checkSnRegistration(snAuth)
        }

        override fun onSnAuthError(error: SnAuthError) {
            error.message?.let { errorMessage -> viewState.showToast(errorMessage) }
        }
    }

    override fun onVkClick() {
        setSnAuthListener()
        viewState.startVkAuthorization()
    }

    override fun onFbClick() {
        setSnAuthListener()
        viewState.startFbAuthorization()
    }

    override fun onOkClick() {
        setSnAuthListener()
        viewState.startOkAuthorization()
    }

    override fun onLoginClick() {
        viewState.showLogin()
    }

    override fun onEmailClick() {
        viewState.showRegistration()
    }

    private fun checkSnRegistration(snAuth: SnAuth) {
        val userRequest = when (snAuth.snType) {
            SnType.VK -> authRepository.getVkUser()
            SnType.FB -> authRepository.getFbUser()
            SnType.OK -> authRepository.getOkUser()
        }

        viewState.showLoadingDialog()
        compositeDisposable += userRequest
                .flatMap {
                    val checkStatus = authRepository.checkSnRegisterStatus(snAuth.snType.code, it.id)
                    Single.zip<RegisterStatus, SnUserData, Pair<RegisterStatus, SnUser>>(checkStatus, Single.just(it), BiFunction { status, snUser ->
                        status to SnUser(snAuth, snUser)
                    })
                }
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val snUser = it.second
                    val status = it.first
                    if (!status.social_auth_found || !status.user_by_social_confirmed_email) {
                        viewState.hideLoadingDialog()
                        viewState.showRegistration(snUser)
                    } else {
                        authorize(snUser.snAuth)
                    }
                }, {
                    viewState.hideLoadingDialog()
                    it.printStackTrace()
                })
    }

    private fun authorize(snAuth: SnAuth) {
        compositeDisposable += authRepository.authSocialNetwork(snAuth.snType.code, snAuth.token, snAuth.email)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.hideLoadingDialog()
                }, {
                    viewState.hideLoadingDialog()
                    it.printStackTrace()
                })
    }

    private fun setSnAuthListener() {
        SnAuthManager.addOnSnAuthListener(snAuthListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        SnAuthManager.removeOnSnAuthListener(snAuthListener)
    }
}
