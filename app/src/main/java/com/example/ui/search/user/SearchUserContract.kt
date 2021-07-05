package com.example.ui.search.user

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.data.models.user.User
import com.example.ui.search.SearchContract

interface SearchUserContract {
    interface View : SearchContract.View<UserDetail, SearchFilter.UserNew> {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUser(user: UserDetail)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateUser(user: UserDetail)
    }

    interface Presenter : SearchContract.Presenter<UserDetail> {
        fun onUserClick(user: UserDetail)
        fun onUserActionCLick(user: UserDetail)
    }
}
