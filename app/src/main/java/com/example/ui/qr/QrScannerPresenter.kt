package com.example.ui.qr

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.repository.DummyRepository
import javax.inject.Inject

@InjectViewState
class QrScannerPresenter
@Inject constructor(
        private val dummyRepository: DummyRepository
) : MvpPresenter<QrScannerContract.View>(), QrScannerContract.Presenter {

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
       // val event = dummyRepository.getEvent()
       // viewState.showEvent(event)
    }
}
