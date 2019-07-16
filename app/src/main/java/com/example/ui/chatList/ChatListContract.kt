package com.example.ui.chatList

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Speaker
import com.example.data.models.UserChat
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface ChatListContract {
    interface View : BaseContract.View {

        @StateStrategyType(SkipStrategy::class)
        fun openChat(chatId: Int, userId: String, userName: String)

        @StateStrategyType(SkipStrategy::class)
        fun openSearchContact(action: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showEmptyView(isShow: Boolean)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "tab")
        fun selectChats()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "tab")
        fun selectInvites()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "list sources")
        fun clearData()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "list sources")
        fun setChatsData(chats: List<UserChat>, favorites: List<Speaker>)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "list sources")
        fun setInvitesData(chats: List<UserChat>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setChatUnreadMessageCount(chatId: String, count: Int)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "fab")
        fun showAddChatButton()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "fab")
        fun hideAddChatButton()
    }

    interface Presenter : BaseContract.Presenter {
        fun onChatClick(userChat: UserChat)
        fun onChatOnScreen(chatId: Int)
        fun onChatGoneFromScreen(chatId: Int)
        fun onFabAddChatClick()
        fun onEmptyChatsButtonAddChatClick()
        fun onInputClick()
        fun onInputFilterClick()

        fun onShowChatListClick()
        fun onShowInvitesClick()

        fun onItemTake(position: Int)
    }
}
