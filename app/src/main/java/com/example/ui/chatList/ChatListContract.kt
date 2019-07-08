package com.example.ui.chatList

import androidx.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserChat
import com.example.holders.UserChatItem
import com.example.ui.base.BaseContract

interface ChatListContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setChats(data: PagedList<UserChatItem>)

        @StateStrategyType(SkipStrategy::class)
        fun openChat(chatId: Int, userId: String, userName: String)

        @StateStrategyType(SkipStrategy::class)
        fun openSearchContact(action: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showEmptyView(isShow: Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onChatClick(userChat: UserChat)
        fun onChatOnScreen(chat: UserChatItem)
        fun onChatGoneFromScreen(chat: UserChatItem)
        fun onMenuAddChatClick()
        fun onEmptyChatsButtonAddChatClick()
        fun onInputClick()
        fun onInputFilterClick()
    }
}
