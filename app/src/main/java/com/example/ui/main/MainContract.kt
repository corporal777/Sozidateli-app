package com.example.ui.main

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Notification
import com.example.data.models.RemoteNotification
import com.example.ui.base.BaseContract
import com.example.util.OneExecutionByTagStateStrategy

interface MainContract {
    interface View : BaseContract.View {
        @StateStrategyType(SkipStrategy::class)
        fun showBackButton(show: Boolean)

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showLogin()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showRecommendations()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showEvent()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showGreetings()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showChat(chatId: String, userName: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEvent(event: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showOrganization(organization: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showNotification(notification: Notification)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRating(event: String)

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

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "error message")
        fun showErrorMessage(message: String)

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "error message")
        fun hideErrorMessage()
    }

    interface Presenter : BaseContract.Presenter {
        fun onOpenStartDestination()
        fun onOpenNotStartDestination()
        fun onOpenChatDestination(chatId: String?)
        fun onOpenCheckConnectionDestination(check: Boolean)
        fun onHandleAuthLink(email: String, code: String)
        fun onHandleRecoverPasswordLink(email: String, code: String)
        fun onHandleChangeEmailConfirm(email: String, code: String)
        fun onHandleChat(chatId: String, userName: String, notificationId: String)
        fun onHandleEvent(event: String)
        fun onHandleSocialNetworkConfirm(userId: String, code: String)
        fun onHandleNotification(notification: RemoteNotification)
        fun onSetPassword(email: String, code: String, password: String)

        fun onInappHidden()
        fun onInappAcceptClick(inapp: Notification)
        fun onInappCancelClick(inapp: Notification)
        fun onInappOkClick()

        fun onRetryConnectionClick()

        fun onRequestShowErrorMessage(message: String)
        fun onRequestHideErrorMessage()

        fun onStoriesComplete()
    }
}
