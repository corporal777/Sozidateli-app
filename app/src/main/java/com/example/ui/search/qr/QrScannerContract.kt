package com.example.ui.search.qr

import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface QrScannerContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "preview")
        fun startPreview()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "preview")
        fun showNoPermission()

        @Skip
        fun showAppSettings()

        @Skip
        fun showEvent(eventId: /*Event*/String)

        @Skip
        fun showEnterCode()

        @OneExecution
        fun showEventNotFoundError()
    }

    interface Presenter : BaseContract.Presenter {
        fun onDecodeQrCode(code: String)
        fun onEnterCodeClick()
        fun onRequestPermissionClick()
    }
}
