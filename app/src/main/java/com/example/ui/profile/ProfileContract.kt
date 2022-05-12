package com.example.ui.profile

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserDetail
import com.example.data.models.user.User
import com.example.ui.base.BaseContract

interface ProfileContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUser(user: UserDetail)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showProfile(uid: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showFavorites()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEvents()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutApp()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showBanned()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openSupportEmail(uid: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openPlayMarket()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSettings()

        @StateStrategyType(SkipStrategy::class)
        fun emailSuccess()

        @StateStrategyType(SkipStrategy::class)
        fun hideDialogProgress()

        @StateStrategyType(SkipStrategy::class)
        fun phoneSuccess(phone: String)

        @StateStrategyType(SkipStrategy::class)
        fun codeSuccess()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUserState(hasBase: Boolean, hasMax: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showEmailNotUnique(email: String)

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneNotUnique(phone: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showQrScannerToAuthWebSite()
    }

    interface Presenter : BaseContract.Presenter {
        fun onProfileClick()
        fun onFavoritesClick()
        fun onEventsClick()
        fun onAboutApplicationClick()
        fun onBannedClick()
        fun onSupportClick()
        fun onRateClick()
        fun onLogoutClick()
        fun onSettingsClick()
        fun sendEmail(email: String)
        fun sendPhone(phone: String)
        fun confirmCode(phone: String, code: String)
        fun checkEmailIsUnique(email: String)
        fun checkPhoneIsUnique(phone: String)
        fun onQrScannerToAuthWebClick()
    }
}
