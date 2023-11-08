package com.example.ui.chatList.contacts

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ChatRoomWithMeModel
import com.example.data.models.UserChat
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract

interface ChatListContract {
    interface View : BaseContract.View {

        @StateStrategyType(SkipStrategy::class)
        fun openChat(chatId: Int, userName: String, avatar : String?)

        @StateStrategyType(SkipStrategy::class)
        fun openSearch()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setChatsData(chats: List<UserChat?>, favorites: List</*User*/UserDetail>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setChatUnreadMessageCount(chatId: String, count: Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
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
