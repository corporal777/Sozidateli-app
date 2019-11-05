package com.example.ui.chatList.invites

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserChat
import com.example.ui.base.BaseContract

interface InviteListContract {
    interface View : BaseContract.View {
        @StateStrategyType(SkipStrategy::class)
        fun openChat(chatId: Int, userName: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setInvitesData(chats: List<UserChat?>)
    }

    interface Presenter : BaseContract.Presenter {
        fun onChatClick(userChat: UserChat)
        fun onItemTake(position: Int)
    }
}
