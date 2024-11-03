package com.example.ui.search

import com.example.data.models.SearchFilter
import com.example.ui.base.BaseContract
import com.example.util.OneExecutionByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface SearchContract {
    interface View<I, F : SearchFilter> : BaseContract.View {
        @OneExecution
        fun showFilter(filter: F)

        @OneExecution
        fun hideFilter()

        @OneExecution
        fun clearFilter()

        @Skip
        fun setHasFilter()
    }

    interface Presenter<I> : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onResume(searchInterface: SearchInterface)
        fun onFilterApplyClick()
        fun onFilterClearClick()
        fun onFilterCancel()
        fun onRefreshRequest()
        fun isHasFilter() : Boolean
        fun getSearchType(): String
    }
}
