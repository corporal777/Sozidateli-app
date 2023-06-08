package com.example.ui.organizations.events

import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EventNew
import com.example.ui.event.list.EventListContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter

interface OrganizationEventsContract {
    interface View : EventListContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun setData(events: List<EventNew?>)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun showEmptyListPlaceholder()
    }

    interface Presenter : EventListContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onRefreshRequest()
    }
}
