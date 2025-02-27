package com.example.ui.search.qr

import android.Manifest
import android.net.Uri
import com.example.data.AppData
import com.example.data.models.EventNew
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withDelay
import withProgressBarLoading
import javax.inject.Inject

@InjectViewState
class QrScannerPresenter
@Inject constructor(
    private val eventRepository: EventRepository,
    private val rxPermissions: RxPermissions,
    appData: AppData
) : BasePresenter<QrScannerContract.View>(appData), QrScannerContract.Presenter {

    private var isPermissionGranted = false
    private var isFirstLaunch = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += rxPermissions.request(Manifest.permission.CAMERA)
            .doOnNext { isPermissionGranted = it }
            .performOnBackgroundOutOnMain()
            .subscribeBy(
                onError = { it.printStackTrace() },
                onNext = {
                    if (isPermissionGranted) viewState.startPreview()
                    else viewState.showNoPermission()
                })
    }

    override fun attachView(view: QrScannerContract.View?) {
        super.attachView(view)
        if (isFirstLaunch) isFirstLaunch = false
        else compositeDisposable += Completable.complete()
            .withDelay(900)
            .andThen(Observable.defer {
                if (isPermissionGranted) Observable.just(true)
                else rxPermissions.request(Manifest.permission.CAMERA)
            })
            .doOnNext { isPermissionGranted = it }
            .performOnBackgroundOutOnMain()
            .withProgressBarLoading(viewState)
            .subscribeSimple {
                if (isPermissionGranted) viewState.startPreview()
                else viewState.showNoPermission()
            }
    }

    override fun onDecodeQrCode(code: String) {
        val eventCode =
            if (!Uri.parse(code).getQueryParameter("code").isNullOrEmpty())
                Uri.parse(code).getQueryParameter("code") ?: ""
            else Uri.parse(code).lastPathSegment ?: ""

        compositeDisposable += eventRepository.getEventByCode(eventCode)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showEventNotFoundError()
                },
                onSuccess = { viewState.showEvent(it.id.toString()) })
    }

    override fun onEnterCodeClick() = viewState.showEnterCode()

    override fun onRequestPermissionClick() = viewState.showAppSettings()
}
