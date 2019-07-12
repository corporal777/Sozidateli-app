package com.example.ui.views.chatView

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

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