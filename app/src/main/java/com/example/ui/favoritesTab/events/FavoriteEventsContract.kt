package com.example.ui.favoritesTab.events

import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import com.example.ui.base.BaseContract
import com.example.util.pagination.PaginationListGroupAdapter
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface FavoriteEventsContract {
    interface View : BaseContract.View {
        @AddToEndSingle
        fun setData(events: List<EventNew?>)

        @OneExecution
        fun updateEventFavorite(eventId: String, isFavorite: Boolean)

        @OneExecution
        fun showAboutEvent(event: String)

        @OneExecution
        fun showSubEvents(event: String, subEvents: List<EventActivityModel>)

        @Skip
        fun showEmptyListPlaceholder()
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback{
        fun onShowEventClick(event: String?)
        fun onEventActionClick(event: EventNew)
        fun onEventSubEventsClick(event: EventNew)
        fun onRefreshRequest()
    }
}
