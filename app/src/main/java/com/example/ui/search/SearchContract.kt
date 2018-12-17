package com.example.ui.search

import android.arch.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.holders.SearchEventResultItem
import com.example.ui.base.BaseContract
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.Query

interface SearchContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSearchData(searchHolder: SearchHolder)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showTypeEvent(data: DataArgsSearchType)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPlaces(data: DataArgsSearchType)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showDateDialog(date:Long,type:String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updatePlacesList(searchHolder: SearchHolder)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateTypeEventsList(searchHolder: SearchHolder)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSearchResult(data:PagedList<SearchEventResultItem>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEvent(event:Event)
    }

    interface Presenter : BaseContract.Presenter {
        fun removePlacesItem(searchTypeEvent: SearchTypeEvent)
        fun removeTypeEventItem(searchTypeEvent: SearchTypeEvent)
        fun onSearchTextChange(text:String)
        fun onPlacesClick()
        fun onTypeEventsClick()
        fun onClickDate(type:String)
        fun onDateSelected(date:Long, type:String)
        fun onSearchClick()
        fun onEventClick(event: Event)
    }
}
