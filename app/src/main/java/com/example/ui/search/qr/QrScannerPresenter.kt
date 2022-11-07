package com.example.ui.search.qr

import android.Manifest
import android.net.Uri
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.EventNew
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withProgressBarLoadingDialog
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
        viewState.setAppBarElevation(0f)
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
        val uri = Uri.parse(code)
        val codee = uri.getQueryParameter("code")
        val parsedCode = codee ?: uri.lastPathSegment
        if (parsedCode == null) viewState.showEventNotFoundError()
        else {
            compositeDisposable += eventRepository.getEventsList(
                mapOf(
                    EventNew.EVENT_LIMIT to 1, EventNew.EVENT_OFFSET to 0,
                    EventNew.EVENT_BINDS to "rights,organization,tag,page,activity,user-registration,user-form-result,current-user-registration,destination-scheme,eventRegistrationState",
                    EventNew.EVENT_CODE to parsedCode
                )
            )
                .performOnBackgroundOutOnMain()
                .withProgressBarLoadingDialog(viewState)
                .subscribeSimple(
                    onError = {
                        viewState.showEventNotFoundError()
                        it.printStackTrace()
                    }, onSuccess = {
                        if (it.data.isNotEmpty()){
                            viewState.showEvent(it.data[0]?.id.toString())
                        }else {
                            viewState.showEventNotFoundError()
                        }

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
