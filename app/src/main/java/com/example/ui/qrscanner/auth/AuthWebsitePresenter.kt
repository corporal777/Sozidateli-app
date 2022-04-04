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
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class AuthWebsitePresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val socket: SocketIOManager,
    private val notificationManager: NotificationManager,
    private val authRepository: AuthRepository
) : BasePresenter<AuthWebsiteContract.View>(appData), AuthWebsiteContract.Presenter {

    private var mToken = ""


    override fun initToken(str: String) {
        this.mToken = str
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += authRepository.sendQrCode(QrBody(mToken, null))
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onSuccess = {
                    viewState.setEnterData(it)
                }
            )

    }

    override fun getEnterData() {

    }


    override fun onConfirmEnterToWebsiteClick() {
        compositeDisposable += authRepository.authWebWithQrCode(QrBody(mToken, true))
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onSuccess = {
                    Log.d("DATA", it.toString())
                    viewState.showSuccessEnterMessage()
                })

    }

    override fun onDoNotConfirmToEnterWebsiteClick() {
        viewState.showEventList()
    }


}