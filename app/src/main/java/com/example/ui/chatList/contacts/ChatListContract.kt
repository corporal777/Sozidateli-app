package com.example.ui.chatList.contacts

import com.example.data.models.ChatRoomWithMeModel
import com.example.data.models.UserChat
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ChatListContract {
    interface View : BaseContract.View {

        @Skip
        fun openChat(chatId: Int, userName: String, avatar : String?)

        @Skip
        fun openSearch()

        @OneExecution
        fun setChatsData(chats: List<UserChat?>, favorites: List</*User*/UserDetail>)

        @OneExecution
        fun setChatUnreadMessageCount(chatId: String, count: Int)

        @OneExecution
        fun setChatUnreadMessage(chatId: String, message: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onChatClick(userChat: UserChat)
        fun onUserClick(uid: Int, userName: String, avatar : String?, chatRoomWithMe: ChatRoomWithMeModel?)
        fun onAddChatClick()
        fun onItemTake(position: Int)
        fun onRefreshRequest()
    }
}
