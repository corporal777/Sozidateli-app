package com.example.ui.organizations.events

import androidx.paging.PagingData
import com.example.data.models.EventNew
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface OrganizationEventsContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setData(data: PagingData<EventNew>, isTemporary : Boolean)

        @OneExecution
        fun showAboutEvent(event: String)

        @OneExecution
        fun showEventRequest(event: String)

        @OneExecution
        fun showAuthorization()

        @Skip
        fun updateEvent(event: EventNew)
    }

    interface Presenter : BaseContract.Presenter {
        fun onActionRegister(event: EventNew, withAccept : Boolean)
        fun onActionCancel(event: EventNew)
        fun onShowEventClick(event: String)
        fun onShowAuthorization(event: String)
        fun onRefreshRequest()
    }
}
