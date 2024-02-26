package com.example.ui.qrscanner

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution

interface QrScannerToAuthWebContract {

    interface View : BaseContract.View {

        @OneExecution
        fun showAuthWebsite(code : String, socketId : String)

        @OneExecution
        fun showErrorScanningMessage()

        @OneExecution
        fun startPreview()
    }

    interface Presenter : BaseContract.Presenter {
        fun onErrorScanning()
        fun onSuccessScanning(code : String)

    }

}