package com.example.ui.chatList.contacts

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ChatRoomWithMeModel
import com.example.data.models.Speaker
import com.example.data.models.UserChat
import com.example.data.models.UserDetail
import com.example.data.models.user.User
import com.example.ui.base.BaseContract

interface ChatListContract {
    interface View : BaseContract.View {

        @StateStrategyType(SkipStrategy::class)
        fun openChat(chatId: Int, userName: String)

        @StateStrategyType(SkipStrategy::class)
        fun openSearch()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setChatsData(chats: List<UserChat?>, favorites: List</*User*/UserDetail>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setChatUnreadMessageCount(chatId: String, count: Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun checkScrollPosition()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun scrollToTopPosition()
    }

    interface Presenter : BaseContract.Presenter {
        fun onChatClick(userChat: UserChat)
        fun onUserClick(uid: Int, userName: String, chatRoomWithMe: ChatRoomWithMeModel?)
        fun onChatOnScreen(chatId: Int)
        fun onChatGoneFromScreen(chatId: Int)
        fun onFabAddChatClick()
        fun onEmptyChatsButtonAddChatClick()
        fun onItemTake(position: Int)
        fun onChatScrollChange(isTopPosition: Boolean)
        fun onRefreshRequest()
    }
}
