package com.example.ui.event.favorite.subevent

import com.example.data.models.EventActivityModel
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution

interface FavoriteSubeventContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setData(data: Map<Long?, List<EventActivityModel>>)

        @OneExecution
        fun showSubEvent(eventId: String, subEventId: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onChangeFavoriteRequest(subevent: EventActivityModel)
        fun onSubEventClick(subEvent: EventActivityModel)
        fun onRefreshRequest()
    }
}