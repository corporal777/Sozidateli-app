package com.example.ui.search.chat

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.data.models.user.User
import com.example.ui.base.BaseContract
import com.example.ui.search.user.SearchUserContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter

interface SearchChatContract {
    interface View : BaseContract.View {

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "list data")
        //fun setUsersData(favorites: List<UserDetail>, chats: List<UserDetail>, another: List<UserDetail>)
        fun setUsersData(users: List<UserDetail?>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUser(user: UserDetail)

        @StateStrategyType(SkipStrategy::class)
        fun showEmptyDataPlaceholder()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showFilter(filter: SearchFilter.UserNew)

        @StateStrategyType(SkipStrategy::class)
        fun changeAppBarElevation(value : Float)
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback, BaseContract.OnChangeElevation {
        fun onUserClick(user: UserDetail)
        fun onFilterClick()
        fun onFilterApplyClick()
        fun onRefreshRequest()
        fun onSearchTextChange(text : String)
    }
}
