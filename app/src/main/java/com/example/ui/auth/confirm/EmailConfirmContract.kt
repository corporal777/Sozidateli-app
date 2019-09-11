package com.example.ui.auth.confirm

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface EmailConfirmContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEmail(email: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setTimeLeft(seconds: Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setCanResend(canResend: Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onResendClick()
    }
}
