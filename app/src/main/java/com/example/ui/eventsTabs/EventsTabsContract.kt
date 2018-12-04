package com.example.ui.eventsTabs

import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface EventsTabsContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "TAB")
        fun selectRecommendedTab()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "TAB")
        fun selectSubscriptionsTab()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "TAB")
        fun selectEventsTab()
    }

    interface Presenter : BaseContract.Presenter {
        fun onRecommendedTabClick()
        fun onSubscriptionsClick()
        fun onEventsClick()
    }
}
