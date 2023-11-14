package com.example.ui.search.chat

import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface SearchChatContract {
    interface View : BaseContract.View {

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "list data")
        fun setUsersData(users: List<UserDetail?>)

        @AddToEndSingle
        fun showEmptyDataPlaceholder()

        @OneExecution
        fun showUser(user: UserDetail)

        @OneExecution
        fun showCurrentUser()

        @OneExecution
        fun showFilter(filter: SearchFilter.UserNew)

        @OneExecution
        fun setFiltersChosen(isChosen: Boolean)

        @Skip
        fun changeAppBarElevation(value: Float)
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback,
        BaseContract.OnChangeElevation {
        fun onUserClick(user: UserDetail)
        fun onFilterClick()
        fun onFilterApplyClick()
        fun onRefreshRequest()
        fun onSearchTextChange(text: String)
    }
}
