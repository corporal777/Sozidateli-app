package com.example.ui.main

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface MainContract {
    interface View : BaseContract.View {
        @StateStrategyType(SkipStrategy::class)
        fun showBackButton(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun initWithAuth()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun initWithEventList()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun initWithEvent()
    }

    interface Presenter : BaseContract.Presenter {
        fun onOpenStartDestination()
        fun onOpenNotStartDestination()
        fun onHandleAuthLink(email: String, code: String)
    }
}
