package com.example.ui.user

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.user.User
import com.example.ui.base.BaseContract

interface UserContract {
    interface View : BaseContract.View {

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setUser(user: User)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openChat(userName: String, chatId: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onWriteMsgClick()
        fun onUnbanClick()
    }
}
