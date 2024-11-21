package com.example.ui.event.my

import androidx.paging.PagingData
import com.example.data.models.EventNew
import com.example.data.models.MyEventsFilter
import com.example.data.models.SearchFilter
import com.example.ui.event.list.EventListContractNew
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface MyEventsContract {
    interface View : EventListContractNew.View {
        @Skip
        fun showEmptyListPlaceholder(show : Boolean)

        @OneExecution
        fun setData(data: PagingData<EventNew>)

        @OneExecution
        fun showFilters()

        @Skip
        fun setFiltersChosen(isChosen : Boolean)

        @Skip
        fun setShowScheduleEvents(canShow: Boolean)
    }

    interface Presenter : EventListContractNew.Presenter {
        fun onSearchTextChange(text: String)
        fun onEventStateClick(isChecked : Boolean, filter: MyEventsFilter)
        fun onShowFiltersClick()
        fun onApplyFiltersClick(filter: SearchFilter.EventNew)
    }
}