package com.example.ui.splash

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface SplashContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun initWithAuth()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun initWithEventList()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun initWithEvent()
    }

    interface Presenter : BaseContract.Presenter
}
