package com.example.ui.search.user

import android.view.ViewGroup
import androidx.paging.PagingData
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.ui.search.SearchContract
import com.example.util.OneExecutionByTagStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface SearchUserContract {
    interface View : SearchContract.View<SearchFilter.UserNew> {
        @OneExecution
        fun setData(data: PagingData<UserDetail>)

        @OneExecution
        fun showUser(user: UserDetail)

        @OneExecution
        fun showCurrentUser()

        @OneExecution
        fun updateUser(user: UserDetail)
    }

    interface Presenter : SearchContract.Presenter<SearchFilter.UserNew> {
        fun onUserClick(user: UserDetail)
        fun onUserActionCLick(user: UserDetail)
    }
}
