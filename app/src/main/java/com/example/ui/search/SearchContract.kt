package com.example.ui.search

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SearchFilter
import com.example.ui.base.BaseContract
import com.example.util.OneExecutionByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter

interface SearchContract {
    interface View<I, F : SearchFilter> : BaseContract.View {
        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "list data")
        fun setData(data: List<I?>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showFilter(filter: F)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideFilter()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun clearFilter()
    }

    interface Presenter<I> : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onResume(searchInterface: SearchInterface)
        fun onFilterApplyClick()
        fun onFilterClearClick()
        fun onFilterCancel()
        fun onRefreshRequest()
    }
}
