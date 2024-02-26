package com.example.ui.qrscanner.auth

import com.example.data.models.QrAuthResponse
import com.example.ui.base.BaseContract
import com.example.ui.views.loading.CustomLoadingButton
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface AuthWebsiteContract {

    interface View : BaseContract.View {
        @OneExecution
        fun setEnterData(data : QrAuthResponse)

        @OneExecution
        fun showEventList()

        @Skip
        fun showContent()

        @Skip
        fun hideContent()

        @Skip
        fun showCustomLoading(view: CustomLoadingButton)

        @Skip
        fun hideCustomLoading(view: CustomLoadingButton)
    }

    interface Presenter : BaseContract.Presenter {
        fun onConfirmAuthClick(view: CustomLoadingButton)
        fun onNotConfirmAuthClick(view: CustomLoadingButton)
    }
}