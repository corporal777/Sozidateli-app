package com.example.ui.main

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface MainContract {
    interface View : BaseContract.View {
        @StateStrategyType(SkipStrategy::class)
        fun showBackButton(show: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showLogin()

        @StateStrategyType(SkipStrategy::class)
        fun showEventList(popUpTo: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showEvent()

        @StateStrategyType(SkipStrategy::class)
        fun showGreetings()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showChat(chatId: String, userName: String)

        @StateStrategyType(SkipStrategy::class)
        fun showDialogRecoverPassword(email: String, code: String)

        @StateStrategyType(SkipStrategy::class)
        fun showDialogChangeEmailSuccess()

        @StateStrategyType(SkipStrategy::class)
        fun showDialogChangeEmailError()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun checkIntent()
    }

    interface Presenter : BaseContract.Presenter {
        fun onOpenStartDestination()
        fun onOpenNotStartDestination()
        fun onOpenChatDestination(chatId: String?)
        fun onHandleAuthLink(email: String, code: String)
        fun onHandleRecoverPasswordLink(email: String, code: String)
        fun onHandleChangeEmailConfirm(email: String, code: String)
        fun onHandleChat(chatId: String, userName: String, notificationId: String)
        fun onHandleSocialNetworkConfirm(snType:String,id:String,code:String)
        fun onSetPassword(email: String, code: String, password: String)
    }
}
