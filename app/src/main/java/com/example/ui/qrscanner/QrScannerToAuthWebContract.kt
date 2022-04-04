package com.example.ui.qrscanner

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract

interface QrScannerToAuthWebContract {

    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun goToAuthWebsite(code : String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showErrorScanningMessage()
    }

    interface Presenter : BaseContract.Presenter {
        fun onEnterProfileWebsiteClick()
        fun onErrorScanning()
        fun onSuccessScanning(code : String)

    }

}