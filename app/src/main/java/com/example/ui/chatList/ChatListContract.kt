package com.example.ui.chatList

import android.arch.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserChat
import com.example.ui.base.BaseContract

interface ChatListContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(data: PagedList<UserChat>)

        @StateStrategyType(SkipStrategy::class)
        fun openChat(chatId: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onChatClick(userChat: UserChat)
    }
}
