package com.example.ui.profile

import android.content.Context
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract

interface ProfileContract {
    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUser(user: UserDetail)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUserState(hasBase: Boolean, hasMax: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUserLink(user: UserDetail)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setChangeOrAddNewAccount(description : Int, icon : Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showProfile(uid: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showChangeAccount()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showStates()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showFavorites()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutApp()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openSupportEmail(uid: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSupport()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openPlayMarket()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSettings()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSessions()

        @StateStrategyType(SkipStrategy::class)
        fun codeSuccess()

        @StateStrategyType(SkipStrategy::class)
        fun showEmailNotUnique(email: String)

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneNotUnique(phone: String)

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneConfirmation(phone: String)

        @StateStrategyType(SkipStrategy::class)
        fun hideAddPhoneEmailDialog()

        @StateStrategyType(SkipStrategy::class)
        fun showEmailConfirmation(email: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showQrScannerToAuthWebSite()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUserProfileLinkDialog(user: UserDetail)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showChangeUserShortNameDialog(user: UserDetail)
    }

    interface Presenter : BaseContract.Presenter {
        fun onProfileClick()
        fun onFavoritesClick()
        fun onSessionsClick()
        fun onChangeAccountClick()
        fun onAboutApplicationClick()
        fun onSupportClick()
        fun onWriteEmailClick()
        fun onRateClick()
        fun onLogoutClick()
        fun onSettingsClick()

        fun onShowEmailConfirm(email: String)
        fun onShowPhoneConfirm(phone: String)
        fun checkEmailIsUnique(email: String)
        fun checkPhoneIsUnique(phone: String)
        fun onConfirmPhoneSuccess(phone: String)

        fun onQrScannerToAuthWebClick()

        fun onShowUserProfileLink()
        fun onShowChangeUserShortName()
    }
}
