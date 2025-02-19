package com.example.ui.event.list

import com.example.data.models.EventNew
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface EventListContractNew {
    interface View : BaseContract.View {
        @Skip
        fun showEventRequest(event: String)

        @Skip
        fun showAboutEvent(event: String)

        @Skip
        fun showAuthorization()

        @Skip
        fun updateEvent(event: EventNew)
    }

    interface Presenter : BaseContract.Presenter {
        fun onActionRegister(event: EventNew, withAccept : Boolean)
        fun onActionCancel(event: EventNew)
        fun onShowEventClick(event: String)
        fun onShowAuthorization(event: String?)
        fun onRefreshRequest()
    }
}