package com.example.ui.search

import androidx.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.DataArgsSearchType
import com.example.data.models.Event
import com.example.data.models.SearchTypeEvent
import com.example.holders.SearchEventResultItem
import com.example.ui.base.BaseContract

interface SearchContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSearchData(searchHolder: SearchHolder)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showTypeEvent(data: DataArgsSearchType)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPlaces(data: DataArgsSearchType)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showDateDialog(date: Long, type: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateOrganizationList(searchHolder: SearchHolder)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateCategoryList(searchHolder: SearchHolder)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSearchResult(data: PagedList<SearchEventResultItem>,totalCount:Int?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEvent(event: Event)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideSearchResultLabel()

        @StateStrategyType(SkipStrategy::class)
        fun showQrScan()

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: Event)
    }

    interface Presenter : BaseContract.Presenter {
        fun removeOrganizationItem(searchTypeEvent: SearchTypeEvent)
        fun removeCategoryItem(searchTypeEvent: SearchTypeEvent)
        fun onSearchTextChange(text: String)
        fun onOrganizationClick()
        fun onCategoryClick()
        fun onClickDate(type: String)
        fun onDateSelected(date: Long, type: String)
        fun onGoToEventClick(event: Event)
        fun onEventClick(event: Event)
        fun onQrScanClick()
        fun clearFilter()
    }
}
