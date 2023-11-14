package com.example.ui.chatList

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ChatListTabsContract {
    interface View : BaseContract.View {
        @OneExecution
        fun selectTab(position: Int)

        @AddToEndSingle
        fun setChatsCount(count: Int)

        @AddToEndSingle
        fun setInvitesCount(count: Int)

        @Skip
        fun openSearch()
    }

    interface Presenter : BaseContract.Presenter {
        fun onFabAddChatClick()
    }
}
