package com.example.ui.subevent.users

import androidx.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.user.User
import com.example.holders.UserItem
import com.example.ui.base.BaseContract

interface SubeventUserListContract {
    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUsers(users: PagedList<UserItem>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEmptyListPlaceholder()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openChat(userName: String, userAvatar: String?, chatId: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onUserClick(user: User)
    }
}
