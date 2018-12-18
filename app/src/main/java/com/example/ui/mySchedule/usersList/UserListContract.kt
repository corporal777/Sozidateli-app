package com.example.ui.mySchedule.usersList

import androidx.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.User
import com.example.ui.base.BaseContract

interface UserListContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUsers(users: PagedList<User>)
    }

    interface Presenter : BaseContract.Presenter
}
