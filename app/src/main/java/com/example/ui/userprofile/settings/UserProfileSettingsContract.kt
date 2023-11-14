package com.example.ui.userprofile.settings

import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.ui.userprofile.base.BaseUserProfileContract
import com.example.ui.views.CustomCheckView
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface UserProfileSettingsContract {
    interface View : BaseUserProfileContract.View {

        @OneExecution
        fun showChangeEmail(email : String?)

        @OneExecution
        fun showChangePassword()

        @OneExecution
        fun showChangeName(user : UserDetail)

        @OneExecution
        fun showChangeShortName(user : UserDetail)

        @OneExecution
        fun showDeleteProfile()

        @OneExecution
        fun showPhoneEdit(phone : FieldDetails?)

        @OneExecution
        fun showEmailConfirmation(email : String)

        @Skip
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
