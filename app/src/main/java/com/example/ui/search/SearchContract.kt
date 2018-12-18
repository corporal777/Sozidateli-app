package com.example.ui.search

import androidx.paging.PagedList
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
        fun updatePlacesList(searchHolder: SearchHolder)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateTypeEventsList(searchHolder: SearchHolder)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSearchResult(data: PagedList<SearchEventResultItem>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEvent(event: Event)

        @StateStrategyType(SkipStrategy::class)
        fun showQrScan()
    }

    interface Presenter : BaseContract.Presenter {
        fun removePlacesItem(searchTypeEvent: SearchTypeEvent)
        fun removeTypeEventItem(searchTypeEvent: SearchTypeEvent)
        fun onSearchTextChange(text: String)
        fun onPlacesClick()
        fun onTypeEventsClick()
        fun onClickDate(type: String)
        fun onDateSelected(date: Long, type: String)
        fun onSearchClick()
        fun onEventClick(event: Event)
        fun onQrScanClick()
    }
}
