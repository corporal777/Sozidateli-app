package com.example.ui.event.my

import com.example.data.models.EventNew
import com.example.data.models.MyEventsFilter
import com.example.data.models.SearchFilter
import com.example.ui.event.list.EventListContract
import com.example.util.pagination.PaginationListGroupAdapter
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface MyEventsContract {
    interface View : EventListContract.View {
        @AddToEndSingle
        fun showEmptyListPlaceholder(isFirst : Boolean)

        @AddToEndSingle
        fun setData(data: List<EventNew?>)

        @OneExecution
        fun showFilters()

        @Skip
        fun setFiltersChosen(isChosen : Boolean)

        @Skip
        fun setShowScheduleEvents(canShow: Boolean)
    }

    interface Presenter : EventListContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback{
        fun onSearchTextChange(text: String)
        fun onShowFiltersClick()
        fun onRefreshRequest()
        fun onEventStateFiltersClick(isChecked : Boolean, filter: MyEventsFilter)
        fun onSearchFiltersClick(filter: SearchFilter.EventNew)
    }
}