package com.example.ui.qrscanner

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface QrScannerToAuthWebContract {

    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAuthWebsite(code : String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showErrorScanningMessage()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun startPreview()
    }

    interface Presenter : BaseContract.Presenter {
        fun onErrorScanning()
        fun onSuccessScanning(code : String)

    }

}