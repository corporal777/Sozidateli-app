package com.example.ui.event.list.favorite

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import com.example.data.models.SubEvent
import com.example.ui.base.BaseContract
import com.example.ui.event.list.EventListContract
import com.example.util.pagination.PaginationListGroupAdapter

interface FavoriteEventsContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(events: List<EventNew?>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateEventFavorite(eventId: String, isFavorite: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showAboutEvent(event: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSubEvents(event: String, subEvents: List<EventActivityModel>)

        @StateStrategyType(SkipStrategy::class)
        fun showEmptyListPlaceholder()
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback{
        fun onShowEventClick(event: String?)
        fun onEventActionClick(event: EventNew)
        fun onEventSubEventsClick(event: EventNew)
        fun onRefreshRequest()
    }
}
