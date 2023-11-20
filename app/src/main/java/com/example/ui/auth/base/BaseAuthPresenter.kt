package com.example.ui.auth.base

import android.util.Log
import com.example.data.AppData
import com.example.data.models.ApiError
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
        private val snAuthManager: SnAuthManager,
        appData: AppData
) : BasePresenter<V>(appData), BaseAuthContract.Presenter {

    protected val snAuthListener = object : SnAuthManager.OnSnAuthListener {
        override fun onSnAuthComplete(snAuth: SnAuth) {
            onContinueWithSnRegistration(snAuth)
        }

        override fun onSnAuthError(error: SnAuthError) {
            error.message?.let { _ -> viewState.showRequestErrorMessage() }
        }
    }

    override fun authVk() {
        compositeDisposable += checkInternetAndRun {
            snAuthManager.apply {
                addOnSnAuthListener(snAuthListener)
                startAuthVk(arrayOf(VKScope.EMAIL))
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        snAuthManager.removeOnSnAuthListener(snAuthListener)
    }

    abstract fun onContinueWithSnRegistration(SnAuth: SnAuth)

    companion object {
        private const val ERROR_NEED_REGISTRATION = "NEED_REGISTRATION"
    }
}
