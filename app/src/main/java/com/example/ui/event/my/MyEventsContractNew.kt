package com.example.ui.event.my

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EventNew
import com.example.data.models.MyEventsFilter
import com.example.ui.event.list.EventListContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter

interface MyEventsContractNew {


    interface View : EventListContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(data: List<EventNew?>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showFilters()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setFiltersChosen(isChosen : Boolean)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun showEmptyListPlaceholder(isFirst : Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setActionButton(event: EventNew?)

        @StateStrategyType(AddToEndSingleStrategy::class)
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