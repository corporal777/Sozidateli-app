package com.example.ui.event.list.recommendations

import com.example.data.models.EventNew
import com.example.ui.event.list.EventListContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface RecommendationsContract {
    interface View : EventListContract.View{
        @OneExecution
        fun setData(events: List<EventNew?>, isNeedUpdateApp : Boolean?)

        @OneExecution
        fun showEmptyListPlaceholder()

        @OneExecution
        fun showSearch()

        @OneExecution
        fun showUserProfile()

        @Skip
        fun setAuthorizationButton(isTemporary : Boolean)
    }

    interface Presenter : EventListContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onSearchClick()
        fun onShowSavedEventOrProfile(isProfile : Boolean?)
        fun onRefreshRequest()
    }
}
