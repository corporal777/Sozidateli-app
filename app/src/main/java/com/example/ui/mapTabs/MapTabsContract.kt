package com.example.ui.mapTabs

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.ui.base.BaseContract

interface MapTabsContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun initPages()

        @StateStrategyType(SkipStrategy::class)
        fun selectPageAtPosition(position: Int)
    }

    interface Presenter : BaseContract.Presenter {
        fun onPageSelected(position: Int)
    }
}
