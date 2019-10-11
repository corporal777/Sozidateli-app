package com.example.ui.search.event

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.data.models.SearchFilter
import com.example.ui.search.SearchContract

interface SearchEventContract {
    interface View : SearchContract.View<Event, SearchFilter.Event> {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutEvent(event: Event)
    }

    interface Presenter : SearchContract.Presenter<Event> {
        fun onEventClick(event: Event)
    }
}
