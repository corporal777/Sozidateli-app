package com.example.ui.event.list.recommendations

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.event.list.EventListContract

interface RecommendationsContract {
    interface View : EventListContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSearch()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showOrganizations()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showMyEvents()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showChat()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAccount()
    }

    interface Presenter : EventListContract.Presenter {
        fun onSearchClick()
        fun onOrganizationsClick()
        fun onMyEventsClick()
        fun onMenuChatClick()
        fun onMenuAccountClick()
    }
}
