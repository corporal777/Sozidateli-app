package com.example.ui.profile

import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ProfileContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setUser(user: UserDetail)

        @OneExecution
        fun setUserState(hasBase: Boolean, hasMax: Boolean)

        @OneExecution
        fun setUserLink(user: UserDetail)

        @OneExecution
        fun showProfile(uid: String)

        @OneExecution
        fun showChangeAccount()

        @OneExecution
        fun showStates()

        @OneExecution
        fun showFavorites()

        @OneExecution
        fun showAboutApp()

        @OneExecution
        fun openSupportEmail(uid: String)

        @OneExecution
        fun showSupport()

        @OneExecution
        fun openPlayMarket()

        @OneExecution
        fun showSettings()

        @OneExecution
        fun showSessions()

        @Skip
        fun showEmailPhoneNotUnique(email: String?, phone: String?)

        @Skip
        fun showPhoneConfirmation(phone: String)

        @Skip
        fun hideAddPhoneEmailDialog()

        @Skip
        fun showEmailConfirmation(email: String)

        @OneExecution
        fun showQrScannerToAuthWebSite()

        @OneExecution
        fun showUserProfileLinkDialog()

        @OneExecution
        fun showChangeUserShortName()
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

        fun checkEmailIsUnique(withCheck : Boolean, email: String)
        fun checkPhoneIsUnique(withCheck: Boolean, phone: String)

        fun onQrScannerToAuthWebClick()

        fun onShowUserProfileLink()
        fun onShowChangeUserShortName()
    }
}
