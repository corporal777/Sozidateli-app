package com.example.ui.search

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ChatMessage
import com.example.data.models.DataArgsSearchType
import com.example.data.models.SearchTypeEvent
import com.example.data.models.UserChat
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
        fun showSearchResult()

    }

    interface Presenter : BaseContract.Presenter {
        fun removePlacesItem(searchTypeEvent: SearchTypeEvent)
        fun removeTypeEventItem(searchTypeEvent: SearchTypeEvent)
        fun onPlacesClick()
        fun onTypeEventsClick()
        fun onClickDate(type:String)
        fun onDateSelected(date:Long, type:String)
        fun onSearchClick()
    }
}
