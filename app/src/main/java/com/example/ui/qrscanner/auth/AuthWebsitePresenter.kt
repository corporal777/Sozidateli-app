package com.example.ui.qrscanner.auth

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.QrBody
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withDelay
import withProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class AuthWebsitePresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val notificationManager: NotificationManager,
    private val authRepository: AuthRepository
) : BasePresenter<AuthWebsiteContract.View>(appData), AuthWebsiteContract.Presenter {

    var token = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += authRepository.sendQrCode(QrBody(token, null))
            .withDelay(300)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    viewState.hideContent()
                },
                onSuccess = {
                    viewState.apply {
                        setEnterData(it)
                        showContent()
                    }
                }
            )

    }


    override fun onConfirmEnterToWebsiteClick() {
        compositeDisposable += authRepository.authWebWithQrCode(QrBody(token, true))
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onSuccess = {
                    viewState.showEventList()
                })

    }

    override fun onDoNotConfirmToEnterWebsiteClick() {
        compositeDisposable += authRepository.authWebWithQrCode(QrBody(token, false))
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showEventList()
                },
                onSuccess = {
                    viewState.showEventList()
                })
    }


}