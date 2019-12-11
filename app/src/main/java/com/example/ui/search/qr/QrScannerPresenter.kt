package com.example.ui.search.qr

import android.Manifest
import android.net.Uri
import com.arellomobile.mvp.InjectViewState
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
        val uri = Uri.parse(code)
        val parsedCode = uri.lastPathSegment
        if (parsedCode == null) viewState.showEventNotFoundError()
        else {
            compositeDisposable += eventRepository.getEventByCode(parsedCode)
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribe({
                        viewState.showEvent(it.event)
                    }, {
                        viewState.showEventNotFoundError()
                        it.printStackTrace()
                    })
        }
    }

    override fun onEnterCodeClick() {
        viewState.showEnterCode()
    }

    override fun onRequestPermissionClick() {
        viewState.showAppSettings()
    }
}
