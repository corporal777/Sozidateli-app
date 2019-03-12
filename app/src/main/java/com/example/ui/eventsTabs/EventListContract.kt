package com.example.ui.eventsTabs

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface EventListContract {
    interface View : BaseContract.View {
        @StateStrategyType(SkipStrategy::class)
        fun showChat()

        @StateStrategyType(SkipStrategy::class)
        fun showSearch()

        @StateStrategyType(SkipStrategy::class)
        fun showAccount()
    }

    interface Presenter : BaseContract.Presenter {
        fun onMenuChatClick()
        fun onMenuSearchClick()
        fun onMenuAccountClick()
    }
}
