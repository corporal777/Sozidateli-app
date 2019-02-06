package com.example.ui.auth.login

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface LoginContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun startVkAuthorization()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun startFbAuthorization()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun startOkAuthorization()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showWelcome()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLogin()
    }

    interface Presenter : BaseContract.Presenter {
        fun onClickVk()
        fun onClickFb()
        fun onClickOk()
        fun onClickEmail()
    }
}
