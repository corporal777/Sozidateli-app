package com.example.ui.qrscanner.auth

import com.example.data.models.QrAuthResponse
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution

interface AuthWebsiteContract {

    interface View : BaseContract.View {
        @OneExecution
        fun setEnterData(data : QrAuthResponse)

        @OneExecution
        fun showEventList()

        @OneExecution
        fun showContent()

        @OneExecution
        fun hideContent()
    }

    interface Presenter : BaseContract.Presenter {
        fun onConfirmEnterToWebsiteClick()
        fun onDoNotConfirmToEnterWebsiteClick()
    }
}