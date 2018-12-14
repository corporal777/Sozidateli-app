package com.example.ui.main

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface MainContract {
    interface View : BaseContract.View {
        @StateStrategyType(SkipStrategy::class)
        fun showBackButton(show: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun initWithAuth()

        @StateStrategyType(SkipStrategy::class)
        fun initWithEventList()

        @StateStrategyType(SkipStrategy::class)
        fun initWithEvent()
    }

    interface Presenter : BaseContract.Presenter {
        fun onOpenStartDestination()
        fun onOpenNotStartDestination()
    }
}
