package com.example.ui.eventTabs

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.MapInfo
import com.example.data.models.Place
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
        fun showAboutTab(eventId: String)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "tab")
        fun showMapTab(eventName: String, mapInfo: MapInfo?, places: Array<Place>?)

        @StateStrategyType(SkipStrategy::class)
        fun showEventList()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setLabel(label: String)

        @StateStrategyType(SkipStrategy::class)
        fun showChat()

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
        fun onMenuAccountClick()
    }
}
