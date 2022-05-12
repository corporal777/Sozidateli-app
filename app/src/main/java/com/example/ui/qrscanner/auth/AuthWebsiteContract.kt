package com.example.ui.qrscanner.auth

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.QrAuthResponse
import com.example.ui.base.BaseContract

interface AuthWebsiteContract {

    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEnterData(data : QrAuthResponse)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSuccessEnterMessage()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showErrorEnterMessage()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEventList()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showContent()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideContent()


    }

    interface Presenter : BaseContract.Presenter {

        fun onConfirmEnterToWebsiteClick()
        fun onDoNotConfirmToEnterWebsiteClick()
        fun initToken(str : String)
    }
}