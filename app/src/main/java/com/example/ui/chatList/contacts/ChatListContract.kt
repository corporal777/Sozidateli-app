package com.example.ui.chatList.contacts

import androidx.paging.PagingData
import com.example.data.models.ChatRoomWithMeModel
import com.example.data.models.UserChatModel
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ChatListContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setData(data: PagingData<UserChatModel>)

        @Skip
        fun openChat(chatId: String, userName: String, avatar : String?)

        @Skip
        fun openSearch()
    }

    interface Presenter : BaseContract.Presenter {
        fun onChatClick(chat: UserChatModel)
        fun onUserClick(user: UserDetail, chatRoom: ChatRoomWithMeModel?)
        fun onAddChatClick()

        fun onRefreshRequest()
    }
}
