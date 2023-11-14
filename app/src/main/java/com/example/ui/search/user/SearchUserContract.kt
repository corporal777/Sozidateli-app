package com.example.ui.search.user

import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.ui.search.SearchContract
import moxy.viewstate.strategy.alias.OneExecution

interface SearchUserContract {
    interface View : SearchContract.View<UserDetail, SearchFilter.UserNew> {
        @OneExecution
        fun showUser(user: UserDetail)

        @OneExecution
        fun showCurrentUser()

        @OneExecution
        fun updateUser(user: UserDetail)
    }

    interface Presenter : SearchContract.Presenter<UserDetail> {
        fun onUserClick(user: UserDetail)
        fun onUserActionCLick(user: UserDetail)
    }
}
