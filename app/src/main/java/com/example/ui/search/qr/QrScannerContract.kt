package com.example.ui.search.qr

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.ui.base.BaseContract

interface QrScannerContract {
    interface View : BaseContract.View {

        @StateStrategyType(SkipStrategy::class)
        fun checkCameraPermission(grantedResult: (Boolean) -> Unit)

        @StateStrategyType(SkipStrategy::class)
        fun requestCameraPermission()

        @StateStrategyType(SkipStrategy::class)
        fun startPreview()

        @StateStrategyType(SkipStrategy::class)
        fun showEvent(event: Event)

        @StateStrategyType(SkipStrategy::class)
        fun showEnterCode()
    }

    interface Presenter : BaseContract.Presenter {
        fun onCameraPermissionGranted()
        fun onDecodeQrCode(code: String)
        fun onEnterCodeClick()
    }
}
