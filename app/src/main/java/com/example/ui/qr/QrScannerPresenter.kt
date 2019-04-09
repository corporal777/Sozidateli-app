package com.example.ui.qr

import android.content.DialogInterface
import call
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.R
import com.example.repository.DummyRepository
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class QrScannerPresenter
@Inject constructor(
        private val eventRepository: EventRepository
) : BasePresenter<QrScannerContract.View>(), QrScannerContract.Presenter {

    override fun attachView(view: QrScannerContract.View?) {
        super.attachView(view)
        viewState.apply {
            checkCameraPermission {
                if (it) startPreview()
                else requestCameraPermission()
            }
        }
    }

    override fun onCameraPermissionGranted() = viewState.startPreview()

    override fun onDecodeQrCode(code: String) {
        eventRepository.getEventList(limit = 1, offset = 0, qr = code)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    if (it.data.isNotEmpty()) {
                        viewState.showEvent(it.data[0])
                    } else {
                        viewState.showErrorDialog(listOf(R.string.by_qr_not_found_event), DialogInterface.OnDismissListener { viewState.navigateUp() })
                    }
                }, {
                    it.printStackTrace()
                })
                .call(compositeDisposable)
    }

    override fun onError(errors: List<String>) {

    }
}
