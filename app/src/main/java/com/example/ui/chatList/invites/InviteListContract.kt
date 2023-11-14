package com.example.ui.chatList.invites

import com.example.data.models.UserChat
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface InviteListContract {
    interface View : BaseContract.View {
        @Skip
        fun openChat(chatId: Int, userName: String)

        @OneExecution
        fun setInvitesData(chats: List<UserChat?>)
    }

    interface Presenter : BaseContract.Presenter {
        fun onChatClick(userChat: UserChat)
        fun onItemTake(position: Int)
        fun onRefreshRequest()
    }
}
