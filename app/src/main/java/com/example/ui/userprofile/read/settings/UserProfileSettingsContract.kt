package com.example.ui.userprofile.read.settings

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.ui.userprofile.base.BaseUserProfileContract

interface UserProfileSettingsContract {
    interface View : BaseUserProfileContract.View {

        @StateStrategyType(SkipStrategy::class)
        fun showChangeEmail()

        @StateStrategyType(SkipStrategy::class)
        fun showUpdateError(message: String? = null)

        @StateStrategyType(SkipStrategy::class)
        fun showChangePassword()

        @StateStrategyType(SkipStrategy::class)
        fun showChangeShortName(user : UserDetail)

        @StateStrategyType(SkipStrategy::class)
        fun showChangePrivacy()

        @StateStrategyType(SkipStrategy::class)
        fun showDeleteProfile()

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneEdit(phone : FieldDetails?)

        @StateStrategyType(SkipStrategy::class)
        fun showEmailConfirmation(email : String)

        @StateStrategyType(SkipStrategy::class)
        fun showNewChangeEmail(email: String)
    }

    interface Presenter : BaseUserProfileContract.Presenter {
        fun onChangePhoneClick()
        fun onChangePasswordClick()
        fun onChangeEmailClick()
        fun onDeleteEmail()
        fun onDeleteConfirmEmail(email: String)
        fun updateEmail(email: String)

        fun onChangePrivacyConfirm(hidden: Boolean)

        fun onBlockProjectNotificationsClick(hidden: Boolean)
        fun onBlockOrganizationNotificationsClick(hidden: Boolean)
        fun onBlockEventNotificationsClick(hidden: Boolean)

        fun onDeleteProfileClick()
        fun onDeleteProfileConfirm()


        fun showChangeShortNameClick()
    }
}
