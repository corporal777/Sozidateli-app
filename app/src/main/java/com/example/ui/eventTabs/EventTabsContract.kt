package com.example.ui.eventTabs

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface EventTabsContract {
    interface View : BaseContract.View {
        @StateStrategyType(SkipStrategy::class)
        fun showMyScheduleTab()

        @StateStrategyType(SkipStrategy::class)
        fun showScheduleTab()

        @StateStrategyType(SkipStrategy::class)
        fun showAboutTab()

        @StateStrategyType(SkipStrategy::class)
        fun showMapTab()

        @StateStrategyType(SkipStrategy::class)
        fun showEventList()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setLabel(label: String)

        @StateStrategyType(SkipStrategy::class)
        fun showChat()

        @StateStrategyType(SkipStrategy::class)
        fun showSearch()

        @StateStrategyType(SkipStrategy::class)
        fun showAccount()
    }

    interface Presenter : BaseContract.Presenter {
        fun onMyScheduleTabSelected()
        fun onScheduleTabSelected()
        fun onAboutSelected()
        fun onMapTabsSelected()
        fun onToListSelected()

        fun onMenuChatClick()
        fun onMenuSearchClick()
        fun onMenuAccountClick()
    }
}
