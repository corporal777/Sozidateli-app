package com.example.ui.chatList

import com.example.data.models.UserChat
import com.example.ui.base.BaseContract

interface ChatListContract {
    interface View : BaseContract.View{
        fun setData()
        fun openChat(userChat: UserChat)
    }

    interface Presenter : BaseContract.Presenter{
        fun onChatClick(userChat: UserChat)
    }
}
