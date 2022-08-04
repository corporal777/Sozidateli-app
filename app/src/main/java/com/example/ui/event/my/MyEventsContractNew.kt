package com.example.ui.event.my

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.data.models.EventNew
import com.example.data.models.MyEventsFilter
import com.example.data.models.Tag
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter

interface MyEventsContractNew {


    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setTagsBlock(listTags : List<Tag>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSearchBlock()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(data: List<EventNew?>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showFilters()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun showEmptyListPlaceholder()

        @StateStrategyType(SkipStrategy::class)
        fun showAboutEvent(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: String)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setActionButton(
            event: EventNew?
        )
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback, BaseContract.OnChangeElevation {
        fun onSearchTextChange(text: String)
        fun onSearchTextSubmit(text: String)
        fun onShowFiltersClick()
        fun onRefreshRequest()
        fun setEventStateFilter(filter: MyEventsFilter)
        fun updateData()

        fun onActionRegister(event: String)
        fun onActionCancel(event: String, registrationId: String?)
        fun onShowEventClick(event: String)
    }
}