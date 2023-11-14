package com.example.ui.banned

import com.example.data.models.UserChat
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution

interface BannedContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setItems(userChats: List<UserChat?>)

        @OneExecution
        fun openUserInfo(userId: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onUserClick(userChat: UserChat)
        fun onUnblockLick(userChat: UserChat)
        fun onRefreshRequest()
    }
}
