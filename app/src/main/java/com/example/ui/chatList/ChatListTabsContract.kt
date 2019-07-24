package com.example.ui.chatList

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface ChatListTabsContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "tab")
        fun selectChats()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "tab")
        fun selectInvites()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setInvitesCount(count: Int)
    }

    interface Presenter : BaseContract.Presenter {
        fun onChatsSecelted()
        fun onInvitessSecelted()
    }
}
