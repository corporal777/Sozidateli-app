package com.example.ui.search.qr

import android.Manifest
import android.net.Uri
import com.example.data.AppData
import com.example.data.models.EventNew
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withProgressBarLoading
import javax.inject.Inject

@InjectViewState
class QrScannerPresenter
@Inject constructor(
    private val eventRepository: EventRepository,
    private val rxPermissions: RxPermissions,
    appData: AppData
) : BasePresenter<QrScannerContract.View>(appData), QrScannerContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

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
        compositeDisposable += Maybe.fromCallable {
            val codee = Uri.parse(code).getQueryParameter("code")
            codee ?: Uri.parse(code).lastPathSegment ?: ""
        }
            .flatMapSingle { eventRepository.getEventByCode(it) }
            .performOnBackgroundOutOnMain()
            .withProgressBarLoading(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showEventNotFoundError()
                },
                onSuccess = { viewState.showEvent(it.id.toString()) })
    }

    override fun onEnterCodeClick() {
        viewState.showEnterCode()
    }

    override fun onRequestPermissionClick() {
        viewState.showAppSettings()
    }
}
