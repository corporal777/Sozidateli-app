package com.example.ui.qrscanner.auth

import android.app.NotificationManager
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.QrBody
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withDelay
import withLoadingDialog
import withProgressBarLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class AuthWebsitePresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val notificationManager: NotificationManager,
    private val authRepository: AuthRepository
) : BasePresenter<AuthWebsiteContract.View>(appData), AuthWebsiteContract.Presenter {

    var mToken = ""


    override fun initToken(str: String) {
        this.mToken = str
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        compositeDisposable += authRepository.sendQrCode(QrBody(mToken, null))
            .withDelay(1000)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    viewState.hideContent()
                },
                onSuccess = {
                    viewState.setEnterData(it)
                    viewState.showContent()
                }
            )

    }


    override fun onConfirmEnterToWebsiteClick() {
        compositeDisposable += authRepository.authWebWithQrCode(QrBody(mToken, true))
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onSuccess = {
                    Log.d("DATA AUTH QR WEB", it.toString())
                    viewState.showEventList()
                })

    }

    override fun onDoNotConfirmToEnterWebsiteClick() {
        compositeDisposable += authRepository.authWebWithQrCode(QrBody(mToken, false))
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onSuccess = {
                    viewState.showEventList()
                })
    }


}