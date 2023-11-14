package com.example.ui.views.chatView

import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import moxy.MvpView
import moxy.viewstate.strategy.StateStrategyType

class ChatViewContract {

    interface View : MvpView {

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "count")
        fun setMessagesCount(count: String)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "count")
        fun setRequestsCount(count: String)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "count")
        fun hideCounter()
    }

    interface Presenter : BaseContract.Presenter
}