package com.example.ui.banned

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserChat
import com.example.ui.base.BaseContract

interface BannedContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setItems(userChats: List<UserChat?>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openUserInfo(userId: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onUserClick(userChat: UserChat)
        fun onUnblockLick(userChat: UserChat)
    }
}
