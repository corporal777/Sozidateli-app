package com.example.ui.eventTabs

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface EventTabsContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun initialNavigationSetup()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "tab")
        fun showMyScheduleTab()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "tab")
        fun showScheduleTab()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "tab")
        fun showAboutTab()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "tab")
        fun showMapTab()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setCurrentDestinationOnStart()

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

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun finish()
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

        fun onClickBackWhenCurrentNavigationOnTop()
    }
}
