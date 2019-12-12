package com.example.ui.event.list.favorite

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.data.models.SubEvent
import com.example.ui.event.list.EventListContract

interface FavoriteEventsContract {
    interface View : EventListContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateEventFavorite(eventId: String, isFavorite: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSubEvents(event: String, subEvents: List<SubEvent>)
    }

    interface Presenter : EventListContract.Presenter {
        fun onEventActionClick(event: Event)
        fun onEventSubeventsClick(event: Event)
    }
}
