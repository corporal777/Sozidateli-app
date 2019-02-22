package com.example.ui.main

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface MainContract {
    interface View : BaseContract.View {
        @StateStrategyType(SkipStrategy::class)
        fun showBackButton(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLogin()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEventList(popUpTo: Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEvent()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showGreetings()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showChat(userId:String,chatId:String,userName:String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onOpenStartDestination()
        fun onOpenNotStartDestination()
        fun onHandleAuthLink(email: String, code: String)
        fun onHandleChat(userId:String,chatId:String,userName:String,notificationId:String)
    }
}
