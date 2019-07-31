package com.example.ui.user

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.user.User
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface UserContract {
    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUser(user: User)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openChat(userName: String, userAvatar: String?, chatId: String)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "action")
        fun setActionSubscribe()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "action")
        fun setActionUnsubscribe()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "action")
        fun setActionUnblock()
    }

    interface Presenter : BaseContract.Presenter {
        fun onWriteMessageClick()

        fun onSubscribeClick()
        fun onUnsubscribeClick()
        fun onUnblockClick()
    }
}
