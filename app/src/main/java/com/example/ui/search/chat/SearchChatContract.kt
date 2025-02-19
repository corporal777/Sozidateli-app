package com.example.ui.search.chat

import androidx.paging.PagingData
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface SearchChatContract {
    interface View : BaseContract.View {

        @OneExecution
        fun setData(data: PagingData<UserDetail>)

        @OneExecution
        fun showUser(user: UserDetail)

        @OneExecution
        fun showCurrentUser()

        @Skip
        fun updateUser(user: UserDetail)

        @Skip
        fun showFilter(filter: SearchFilter.UserNew)

        @Skip
        fun setFiltersChosen(isChosen: Boolean)

        @Skip
        fun changeAppBarElevation(value: Float)
    }

    interface Presenter : BaseContract.Presenter {
        fun onUserClick(user: UserDetail)
        fun onUserActionCLick(user: UserDetail)
        fun onFilterClick()
        fun onFilterApplyClick(newFilter: SearchFilter.UserNew)
        fun onRefreshRequest()
        fun onSearchTextChange(text: String)

        fun onChangeOffset(offset : Int)
    }
}
