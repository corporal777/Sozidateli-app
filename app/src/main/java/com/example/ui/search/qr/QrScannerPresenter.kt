package com.example.ui.search.qr

import android.Manifest
import android.content.DialogInterface
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class QrScannerPresenter
@Inject constructor(
        private val eventRepository: EventRepository,
        private val rxPermissions: RxPermissions
) : BasePresenter<QrScannerContract.View>(), QrScannerContract.Presenter {

    override fun attachView(view: QrScannerContract.View?) {
        super.attachView(view)
        compositeDisposable += rxPermissions
                .request(Manifest.permission.CAMERA)
                .subscribe({
                    if (it) viewState.startPreview()
                    else viewState.showNoPermission()
                }, {
                    it.printStackTrace()
                })
    }

    override fun onDecodeQrCode(code: String) {
        compositeDisposable += eventRepository.getEventByCode(code)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.showEvent(it)
                }, {
                    viewState.showEventNotFoundError()
                    it.printStackTrace()
                })
    }

    override fun onEnterCodeClick() {
        viewState.showEnterCode()
    }

    override fun onRequestPermissionClick() {
        viewState.showAppSettings()
    }
}
