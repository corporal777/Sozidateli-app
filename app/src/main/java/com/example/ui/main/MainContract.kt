package com.example.ui.main

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Notification
import com.example.data.models.RemoteNotification
import com.example.ui.accountChange.data.AuthType
import com.example.ui.base.BaseContract
import com.example.util.OneExecutionByTagStateStrategy

interface MainContract {
    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showLogin()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showRecommendations()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showGreetings()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showChat(chatId: String, userName: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutEvent(event: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showOrganization(organization: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showNotification(notification: Notification)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRating(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showDialogRecoverPassword(/*email: String,*/ code: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun checkIntent()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showStories()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showInApp(inapp: Notification)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideInApp()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "error message")
        fun showErrorMessage(message: String)

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "error message")
        fun hideErrorMessage()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showInviteRegister(email: String, code: String, name: String, lastName: String, middleName: String, invite: Int)

        @StateStrategyType(OneExecutionByTagStateStrategy::class)
        fun showAuthWebsiteFragment(code : String)

        @StateStrategyType(OneExecutionByTagStateStrategy::class)
        fun showBadgeNotification(show : Boolean)

        @StateStrategyType(OneExecutionByTagStateStrategy::class)
        fun showBadgeChat(show : Boolean)

        @StateStrategyType(OneExecutionByTagStateStrategy::class)
        fun setMainTheme()

        @StateStrategyType(OneExecutionByTagStateStrategy::class)
        fun showAccountChangeFragment(url : String, type : AuthType)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showBrowser(url : String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onOpenStartDestination()
        fun onOpenNotStartDestination()
        fun onOpenChatDestination(chatId: String?)
        fun onOpenCheckConnectionDestination(check: Boolean)
        fun onHandleRecoverPasswordLink(/*email: String,*/ code: String)
        fun onHandleChat(chatId: String, userName: String, notificationId: String)
        fun onHandleEventCode(event: String)
        fun onHandleEvent(event: String)
        fun onHandleAuthToOtherPlatform(url: String, type : AuthType)
        fun onHandleSocialNetworkConfirm(userId: String, code: String)
        fun onHandleNotification(notification: RemoteNotification)
        fun onSetPassword(/*email: String, */code: String, password: String)

        fun onInappHidden()
        fun onInappAcceptClick(inapp: Notification)
        fun onInappCancelClick(inapp: Notification)
        fun onInappOkClick(inapp: Notification)

        fun onRetryConnectionClick()

        fun onRequestShowErrorMessage(message: String)
        fun onRequestHideErrorMessage()

        fun onStoriesComplete()
        fun onInviteRegister(email: String, code: String, name: String, lastName: String, middleName: String, invite: Int)
        fun openPgrfFromInvite(inviteId: String)
        fun openAuthWebsiteFragment(code : String)

        fun onBackClick()
    }
}
