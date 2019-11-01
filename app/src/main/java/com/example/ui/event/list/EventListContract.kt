package com.example.ui.event.list

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.ui.base.BaseContract
import com.example.util.pagination.PaginationListGroupAdapter

interface EventListContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(events: List<Event?>)

        @StateStrategyType(SkipStrategy::class)
        fun scrollToPositionWithOffset(position: Int, offset: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showAboutEvent(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: Event)
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onEventClick(event: Event)
        fun onGoToEventClick(event: Event)
        fun onScrollChange(position: Int, offset: Int)
    }
}
