package com.example.ui.main

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Notification
import com.example.ui.base.BaseContract
import com.example.util.OneExecutionByTagStateStrategy

interface MainContract {
    interface View : BaseContract.View {
        @StateStrategyType(SkipStrategy::class)
        fun showBackButton(show: Boolean)

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showLogin()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showEventList()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showEvent()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showGreetings()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showChat(chatId: String, userName: String)

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showEvent(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showDialogRecoverPassword(email: String, code: String)

        @StateStrategyType(SkipStrategy::class)
        fun showDialogChangeEmailSuccess()

        @StateStrategyType(SkipStrategy::class)
        fun showDialogChangeEmailError()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun checkIntent()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showStories()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showInapp(inapp: Notification)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideInapp()
    }

    interface Presenter : BaseContract.Presenter {
        fun onOpenStartDestination()
        fun onOpenNotStartDestination()
        fun onOpenChatDestination(chatId: String?)
        fun onHandleAuthLink(email: String, code: String)
        fun onHandleRecoverPasswordLink(email: String, code: String)
        fun onHandleChangeEmailConfirm(email: String, code: String)
        fun onHandleChat(chatId: String, userName: String, notificationId: String)
        fun onHandleSocialNetworkConfirm(snType: String, id: String, code: String)
        fun onHandleEvent(event: String)
        fun onSetPassword(email: String, code: String, password: String)

        fun onInappHidden()
        fun onInappAcceptClick(inapp: Notification)
        fun onInappCancelClick(inapp: Notification)
        fun onInappOkClick()
    }
}
