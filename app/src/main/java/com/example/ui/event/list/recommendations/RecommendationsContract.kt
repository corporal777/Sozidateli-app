package com.example.ui.event.list.recommendations

import com.example.data.models.EventNew
import com.example.ui.event.list.EventListContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface RecommendationsContract {
    interface View : EventListContract.View{
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun setData(events: List<EventNew?>, isNeedUpdateApp : Boolean?)

        @AddToEndSingle
        fun showEmptyListPlaceholder()

        @OneExecution
        fun showSearch()

        @OneExecution
        fun showUserProfile()
    }

    interface Presenter : EventListContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onSearchClick()
        fun onProfileClick()
        fun onRefreshRequest()
    }
}
