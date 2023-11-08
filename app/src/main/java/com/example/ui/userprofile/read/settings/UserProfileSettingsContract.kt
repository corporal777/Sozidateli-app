package com.example.ui.userprofile.read.settings

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.ui.userprofile.base.BaseUserProfileContract
import com.example.ui.views.CustomCheckView

interface UserProfileSettingsContract {
    interface View : BaseUserProfileContract.View {

        @StateStrategyType(SkipStrategy::class)
        fun showChangeEmail(email : String?)

        @StateStrategyType(SkipStrategy::class)
        fun showChangePassword()

        @StateStrategyType(SkipStrategy::class)
        fun showChangeName(user : UserDetail)

        @StateStrategyType(SkipStrategy::class)
        fun showChangeShortName(user : UserDetail)

        @StateStrategyType(SkipStrategy::class)
        fun showDeleteProfile()

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneEdit(phone : FieldDetails?)

        @StateStrategyType(SkipStrategy::class)
        fun showEmailConfirmation(email : String)

        @StateStrategyType(SkipStrategy::class)
        fun showBlockingLoading(show : Boolean, checkView : CustomCheckView)
    }

    interface Presenter : BaseUserProfileContract.Presenter {
        fun onChangePhoneClick()
        fun onChangePasswordClick()
        fun onChangeEmailClick()
        fun onDeleteEmail()
        fun onDeleteConfirmEmail(email: String)

        fun onChangePrivacyConfirm(hidden: Boolean, view : CustomCheckView)

        fun onBlockProjectNotificationsClick(hidden: Boolean, view : CustomCheckView)
        fun onBlockOrganizationNotificationsClick(hidden: Boolean, view : CustomCheckView)
        fun onBlockEventNotificationsClick(hidden: Boolean, view : CustomCheckView)

        fun onDeleteProfileClick()
        fun onDeleteProfileConfirm()


        fun showChangeNameClick()
        fun showChangeShortNameClick()
        fun onShowEmailConfirm(email : String)
    }
}
