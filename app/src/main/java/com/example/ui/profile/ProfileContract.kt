package com.example.ui.profile

import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ProfileContract {
    interface View : BaseContract.View {
        @AddToEndSingle
        fun setUser(user: UserDetail)

        @AddToEndSingle
        fun setUserState(hasBase: Boolean, hasMax: Boolean)

        @AddToEndSingle
        fun setUserLink(user: UserDetail)

        @OneExecution
        fun setChangeOrAddNewAccount(description : Int, icon : Int)

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
        fun codeSuccess()

        @Skip
        fun showEmailNotUnique(email: String)

        @Skip
        fun showPhoneNotUnique(phone: String)

        @Skip
        fun showPhoneConfirmation(phone: String)

        @Skip
        fun hideAddPhoneEmailDialog()

        @Skip
        fun showEmailConfirmation(email: String)

        @OneExecution
        fun showQrScannerToAuthWebSite()

        @OneExecution
        fun showUserProfileLinkDialog(user: UserDetail)

        @OneExecution
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
