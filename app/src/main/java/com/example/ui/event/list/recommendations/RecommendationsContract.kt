package com.example.ui.event.list.recommendations

import androidx.paging.PagingData
import com.example.data.models.EventNew
import com.example.ui.event.list.EventListContract
import com.example.ui.event.list.EventListContractNew
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface RecommendationsContract {
    interface View : EventListContractNew.View{
        @OneExecution
        fun setData(data: PagingData<EventNew>, isNeedUpdateApp : Boolean)

        @OneExecution
        fun showSearch()

        @OneExecution
        fun showUserProfile()

        @Skip
        fun setAuthorizationButton(isTemporary : Boolean)
    }

    interface Presenter : EventListContractNew.Presenter {
        fun onSearchClick()
        fun onShowSavedEventOrProfile(isProfile : Boolean?)
    }
}
