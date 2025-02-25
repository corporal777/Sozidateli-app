package com.example.ui.search

import androidx.paging.PagingData
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import com.example.util.OneExecutionByTagStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface SearchContract {
    interface View : BaseContract.View {
        @Skip
        fun setHasFilter()
    }

    interface Presenter: BaseContract.Presenter {
        fun onResume(searchInterface: SearchInterface)
        fun onRefreshRequest()
        fun isHasFilter() : Boolean
        fun onShowFilterRequest()
    }
}
