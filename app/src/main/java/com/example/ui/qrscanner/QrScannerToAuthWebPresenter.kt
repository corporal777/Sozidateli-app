package com.example.ui.qrscanner

import android.Manifest
import android.net.Uri
import android.util.Log
import com.example.data.AppData
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class QrScannerToAuthWebPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val rxPermissions: RxPermissions,
) : BasePresenter<QrScannerToAuthWebContract.View>(appData), QrScannerToAuthWebContract.Presenter {


    override fun attachView(view: QrScannerToAuthWebContract.View?) {
        super.attachView(view)
        compositeDisposable += rxPermissions
            .request(Manifest.permission.CAMERA)
            .subscribe({
                if (it) viewState.startPreview()
                else viewState.navigateUp()
            }, {
                it.printStackTrace()
            })
    }

    override fun onErrorScanning() {
        viewState.showErrorScanningMessage()
    }

    override fun onSuccessScanning(code: String) {
        Log.e("CODE", code)
        val token =
            Uri.parse(code).getQueryParameter("code") ?: Uri.parse(code).lastPathSegment ?: ""
        if (!token.isNullOrEmpty()) {
            viewState.showAuthWebsite(token)
        } else {
            viewState.showErrorScanningMessage()
        }
    }


}