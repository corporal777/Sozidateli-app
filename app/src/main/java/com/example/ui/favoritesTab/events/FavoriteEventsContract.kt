package com.example.ui.favoritesTab.events

import androidx.paging.PagingData
import com.example.data.models.EventNew
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface FavoriteEventsContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setData(events: PagingData<EventNew>)

        @OneExecution
        fun showAboutEvent(event: String)

        @Skip
        fun updateEvent(event: EventNew)
    }

    interface Presenter : BaseContract.Presenter {
        fun onShowEventClick(event: String)
        fun onEventActionClick(event: EventNew)
        fun onRefreshRequest()
    }
}
