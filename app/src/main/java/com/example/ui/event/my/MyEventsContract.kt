package com.example.ui.event.my

import com.example.data.models.EventNew
import com.example.data.models.MyEventsFilter
import com.example.ui.event.list.EventListContract
import com.example.util.pagination.PaginationListGroupAdapter
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface MyEventsContract {
    interface View : EventListContract.View {
        @AddToEndSingle
        fun setData(data: List<EventNew?>)

        @OneExecution
        fun showFilters()

        @OneExecution
        fun setFiltersChosen(isChosen : Boolean)

        @AddToEndSingle
        fun showEmptyListPlaceholder(isFirst : Boolean)

        @OneExecution
        fun setActionButton(event: EventNew?)

        @AddToEndSingle
        fun setShowMyScheduleButton(canShow: Boolean)
    }

    interface Presenter : EventListContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback{
        fun onSearchTextChange(text: String)
        fun onSearchTextSubmit(text: String)
        fun onShowFiltersClick()
        fun onRefreshRequest()
        fun setEventStateFilter(isChecked : Boolean, filter: MyEventsFilter)
        fun updateData()
    }
}