package com.example.ui.event.list.recommendations

import androidx.paging.PagingData
import com.example.data.models.EventNew
import com.example.ui.event.list.EventListContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface RecommendationsContract {
    interface View : EventListContract.View{
        @OneExecution
        fun setData(data: PagingData<EventNew>, isNeedUpdateApp : Boolean)

        @OneExecution
        fun showSearch()

        @OneExecution
        fun showUserProfile()

        @Skip
        fun setAuthorizationButton(isTemporary : Boolean)
    }

    interface Presenter : EventListContract.Presenter {
        fun onSearchClick()
    }
}
