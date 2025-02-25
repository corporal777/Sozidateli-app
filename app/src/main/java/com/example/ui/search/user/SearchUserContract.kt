package com.example.ui.search.user

import androidx.paging.PagingData
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.ui.search.SearchContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface SearchUserContract {
    interface View : SearchContract.View {
        @OneExecution
        fun setData(data: PagingData<UserDetail>)

        @OneExecution
        fun showUser(user: UserDetail)

        @OneExecution
        fun showCurrentUser()

        @Skip
        fun showFilter(filter: SearchFilter.UserNew)

        @Skip
        fun updateUser(user: UserDetail)
    }

    interface Presenter : SearchContract.Presenter {
        fun onUserClick(user: UserDetail)
        fun onUserActionCLick(user: UserDetail)
        fun onFiltersApplyClick(filter: SearchFilter.UserNew)
    }
}
