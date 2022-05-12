package com.example.ui.search.qr

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface QrScannerContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "preview")
        fun startPreview()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "preview")
        fun showNoPermission()

        @StateStrategyType(SkipStrategy::class)
        fun showAppSettings()

        @StateStrategyType(SkipStrategy::class)
        fun showEvent(eventId: /*Event*/String)

        @StateStrategyType(SkipStrategy::class)
        fun showEnterCode()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEventNotFoundError()
    }

    interface Presenter : BaseContract.Presenter {
        fun onDecodeQrCode(code: String)
        fun onEnterCodeClick()
        fun onRequestPermissionClick()
    }
}
