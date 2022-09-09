package com.example.ui.userprofile.read.settings

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.userprofile.base.BaseUserProfileContract

interface UserProfileSettingsContract {
    interface View : BaseUserProfileContract.View {

        @StateStrategyType(SkipStrategy::class)
        fun showChangeEmail()

        @StateStrategyType(SkipStrategy::class)
        fun showChangeEmailComplete(email: String)

        @StateStrategyType(SkipStrategy::class)
        fun showUpdateError(message: String? = null)

        @StateStrategyType(SkipStrategy::class)
        fun showChangePassword()

        @StateStrategyType(SkipStrategy::class)
        fun showPasswordChangeComplete()

        @StateStrategyType(SkipStrategy::class)
        fun showChangePrivacy()

        @StateStrategyType(SkipStrategy::class)
        fun showDeleteProfile()

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneEdit()

        @StateStrategyType(SkipStrategy::class)
        fun hideDialogProgress()

        @StateStrategyType(SkipStrategy::class)
        fun hideDialogProgress2()

        @StateStrategyType(SkipStrategy::class)
        fun phoneSuccess(phone: String)

        @StateStrategyType(SkipStrategy::class)
        fun passwordSuccess(phone: String)

        @StateStrategyType(SkipStrategy::class)
        fun codeSuccess()

        @StateStrategyType(SkipStrategy::class)
        fun showNewChangeEmail(email: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEmailNotUnique(email: String)

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneNotUnique(phone: String)

        @StateStrategyType(SkipStrategy::class)
        fun showOldPasswordError(attempts : Int)

        @StateStrategyType(SkipStrategy::class)
        fun showNewPasswordTypingContent()


        @StateStrategyType(SkipStrategy::class)
        fun showLoginAgainDialog()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRecoveryPassword(email: String)
    }

    interface Presenter : BaseUserProfileContract.Presenter {
        fun onChangePhoneClick()

        fun onChangePasswordClick()
        fun onChangePasswordClickConfirm(newPassword: String)
        fun onChangeEmailClick()
        fun onChangeEmailConfirm(email: String, isFirst: Boolean)
        fun onDeleteEmail()
        fun onDeleteConfirmEmail(email: String)
        fun registerEmailResend(email: String)
        fun checkEmailIsUnique(email: String, isFirst: Boolean)
        fun checkPhoneIsUnique(phone: String)

        fun onChangePrivacyClick()
        fun onChangePrivacyConfirm(hidden: Boolean)

        fun onBlockProjectNotificationsClick(hidden: Boolean)
        fun onBlockOrganizationNotificationsClick(hidden: Boolean)
        fun onBlockEventNotificationsClick(hidden: Boolean)

        fun onDeleteProfileClick()
        fun onDeleteProfileConfirm()

        fun sendPhone(phone: String)

        fun onChangeNotConfirmedPhone(phone: String)
        fun onPasswordInputComplete(password: String, phone: String)
        fun confirmCode(phone: String, code: String)
        fun checkPasswordValid(password: String)
        fun logoutFromAccount()
    }
}
