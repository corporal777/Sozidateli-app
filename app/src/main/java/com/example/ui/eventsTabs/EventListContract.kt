package com.example.ui.eventsTabs

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface EventListContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "TAB")
        fun selectRecommendedTab()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "TAB")
        fun selectSubscriptionsTab()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "TAB")
        fun selectEventsTab()

        @StateStrategyType(SkipStrategy::class)
        fun showChat()

        @StateStrategyType(SkipStrategy::class)
        fun showSearch()

        @StateStrategyType(SkipStrategy::class)
        fun showAccount()
    }

    interface Presenter : BaseContract.Presenter {
        fun onRecommendedTabClick()
        fun onSubscriptionsClick()
        fun onEventsClick()

        fun onMenuChatClick()
        fun onMenuSearchClick()
        fun onMenuAccountClick()
    }
}
