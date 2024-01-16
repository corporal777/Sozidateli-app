package com.example.ui.main

import com.example.data.models.AuthType
import com.example.data.models.Notification
import com.example.data.models.RemoteNotification
import com.example.data.models.SupportData
import com.example.ui.base.BaseContract
import com.example.util.OneExecutionByTag
import com.example.util.OneExecutionByTagStateStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

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

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showAboutEvent(event: String)

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showUser(userId: String)

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showCurrentUser()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showOrganization(organization: String)

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showRating(event: String)

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showInviteRegister(email: String, code: String, name: String, lastName: String, middleName: String, invite: Int)

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showAuthWebsiteFragment(code : String)

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showAccountChangeFragment(url : String, type : AuthType)

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showSupportQuestion(data : SupportData)

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showProfileSettings()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "content")
        fun showPasswordRecovery()

        @Skip
        fun showChangePassword(userId: String, code: String)

        @OneExecution
        fun checkIntent()

        @OneExecution
        fun showInAppNew(listInApp: List<Notification>)

        @Skip
        fun showErrorMessage(message: String)

        @Skip
        fun hideErrorMessage()

        @Skip
        fun showBadgeNotification(count : Int)

        @Skip
        fun showBadgeChat(count : Int)

        @OneExecution
        fun showBrowser(url : String)

        @OneExecution
        fun showUpdateApp(isRequired : Boolean)

        @OneExecution
        fun showStories()

        @OneExecution
        fun showSplashScreen()

        @OneExecution
        fun hideSplashScreen()

        @Skip
        fun setAppBarElevation(value: Float)

        @Skip
        fun clearIntentData()
    }

    interface Presenter : BaseContract.Presenter {
        fun onOpenStartDestination()
        fun onOpenNotStartDestination()
        fun onOpenChatDestination(chatId: String?)
        fun onOpenCheckConnectionDestination(check: Boolean)
        fun onHandleChangePasswordLink(userId: String, code: String)
        fun onHandleRecoverPasswordLink()
        fun onHandleChat(chatId: String, userName: String, notificationId: String)
        fun onHandleEventCode(event: String?)
        fun onHandleEvent(event: String?)
        fun onHandleUser(userId: String?)
        fun onHandleAuthToOtherPlatform(url: String?, type : AuthType)
        fun onHandleSocialNetworkConfirm(userId: String, code: String)
        fun onHandleNotification(notification: RemoteNotification)

        fun onHandleSupportQuestionLink(id : String?)
        fun onHandleProfileSettingsLink()
        fun onHandleProfileLink()


        fun onRetryConnectionClick()
        fun onStoriesComplete()

        fun onRequestShowErrorMessage(message: String)
        fun onRequestHideErrorMessage()

        fun onInviteRegister(email: String, code: String, name: String, lastName: String, middleName: String, invite: Int)
        fun onHandleAuthWebsite(code : String?)

        fun onBackClick()
    }
}
