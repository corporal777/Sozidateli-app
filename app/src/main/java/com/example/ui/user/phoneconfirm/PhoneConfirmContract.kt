package com.example.ui.user.phoneconfirm

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface PhoneConfirmContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setCanResend(canResend: Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setTimeLeft(time: String?)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setPhone(phone: String)

        @StateStrategyType(SingleStateStrategy::class)
        fun showSendSmsError()

        @StateStrategyType(SingleStateStrategy::class)
        fun showWrongCodeError()

        @StateStrategyType(SingleStateStrategy::class)
        fun onPhoneConfirmationComplete()
    }

    interface Presenter : BaseContract.Presenter {
        fun onResendClick()
        fun onCodeSendClick(code: String)
    }
}
