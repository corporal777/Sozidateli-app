package com.example.ui.search.user

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.data.models.SearchFilter
import com.example.data.models.user.User
import com.example.ui.search.SearchContract

interface SearchUserContract {
    interface View : SearchContract.View<User, SearchFilter.User> {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUser(user: User)
    }

    interface Presenter : SearchContract.Presenter<User> {
        fun onUserClick(user: User)
    }
}
