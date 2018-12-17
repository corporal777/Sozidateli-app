package com.example.ui.recommendations

import androidx.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.ui.base.BaseContract

interface RecommendationsContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(events: PagedList<Event>)

        @StateStrategyType(SkipStrategy::class)
        fun scrollToPositionWithOffset(position: Int, offset: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showAboutEvent(event: Event, vararg sharedElements: Pair<android.view.View, String>)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: Event)
    }

    interface Presenter : BaseContract.Presenter {
        fun onEventClick(event: Event, vararg sharedElements: Pair<android.view.View, String>)
        fun onGoToEventClick(event: Event)
        fun onScrollChange(position: Int, offset: Int)
    }
}
