package com.example.ui.search

import androidx.paging.PagingData
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import com.example.util.OneExecutionByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface SearchContract {
    interface View<F : SearchFilter> : BaseContract.View {
        @OneExecution
        fun showFilter(filter: F)

        @Skip
        fun setHasFilter()

        @Skip
        fun setDataEmpty(isEmpty : Boolean, title : String)

        @Skip
        fun invalidatePagingData()
    }

    interface Presenter<F : SearchFilter> : BaseContract.Presenter {
        fun onResume(searchInterface: SearchInterface)
        fun onRefreshRequest()
        fun isHasFilter() : Boolean
        fun onShowFilterRequest()
        fun onFiltersApplyClick(filter: F)
    }
}
