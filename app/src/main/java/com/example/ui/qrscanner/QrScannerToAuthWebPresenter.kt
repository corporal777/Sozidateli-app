package com.example.ui.qrscanner

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class QrScannerToAuthWebPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val socket: SocketIOManager,
    private val notificationManager: NotificationManager,
    private val authRepository: AuthRepository
) : BasePresenter<QrScannerToAuthWebContract.View>(appData), QrScannerToAuthWebContract.Presenter {


    override fun onEnterProfileWebsiteClick() {

    }

    override fun onErrorScanning() {
        viewState.showErrorScanningMessage()
    }

    override fun onSuccessScanning(code : String) {
        viewState.goToAuthWebsite(code)
    }


}