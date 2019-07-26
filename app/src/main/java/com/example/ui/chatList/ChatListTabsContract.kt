package com.example.ui.chatList

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface ChatListTabsContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun selectTab(position: Int)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setInvitesCount(count: Int)
    }

    interface Presenter : BaseContract.Presenter {
        fun onChatsSelected()
        fun onInvitesSelected()
    }
}
