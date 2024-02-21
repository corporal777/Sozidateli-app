package com.example.ui.qrscanner.auth

import android.app.NotificationManager
import com.example.data.AppData
import com.example.data.bodies.QrBody
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withDelay
import withProgressBarDialogLoading
import withProgressBarLoading
import javax.inject.Inject

@InjectViewState
class AuthWebsitePresenter
@Inject constructor(
    private val appData: AppData,
    private val authRepository: AuthRepository,
    private val socket: SocketIOManager
) : BasePresenter<AuthWebsiteContract.View>(appData), AuthWebsiteContract.Presenter {

    var token = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        //compositeDisposable += authRepository.sendQrCode(QrBody(token, null))
        compositeDisposable += authRepository.sendQrCode(QrBody(token, null))
            .withDelay(300)
            .performOnBackgroundOutOnMain()
            .withProgressBarLoading(viewState)
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
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
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
            .withProgressBarDialogLoading(viewState)
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