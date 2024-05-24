package com.example.ui.organizations.events

import com.example.data.models.EventNew
import com.example.ui.event.list.EventListContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface OrganizationEventsContract {
    interface View : EventListContract.View {
        @OneExecution
        fun setData(events: List<EventNew?>)

        @OneExecution
        fun showEmptyListPlaceholder()
    }

    interface Presenter : EventListContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onRefreshRequest()
    }
}
