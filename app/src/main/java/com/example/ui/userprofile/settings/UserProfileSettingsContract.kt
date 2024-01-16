package com.example.ui.userprofile.settings

import android.content.Context
import android.view.ViewGroup
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.ui.userprofile.base.BaseUserProfileContract
import com.example.ui.views.CustomCheckView
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface UserProfileSettingsContract {
    interface View : BaseUserProfileContract.View {

        @Skip
        fun setUserPassword(isAbsent : Boolean)

        @Skip
        fun setUserSocialBinds(user: UserDetail)

        @OneExecution
        fun showChangeEmail(email : String?)

        @OneExecution
        fun showChangePassword(isChange : Boolean)

        @OneExecution
        fun showChangeName(user : UserDetail)

        @OneExecution
        fun showChangeShortName(user : UserDetail)

        @OneExecution
        fun showDeleteProfile()

        @OneExecution
        fun showChangePhone()

        @OneExecution
        fun showEmailConfirmation(email : String)

        @Skip
        fun showBlockingLoading(show : Boolean, customView : ViewGroup)
    }

    interface Presenter : BaseUserProfileContract.Presenter {
        fun onChangePhoneClick()
        fun onChangePasswordClick()
        fun onChangeEmailClick()
        fun onDeleteEmail()
        fun onDeleteConfirmEmail(email: String)

        fun onChangePrivacyConfirm(hidden: Boolean, view : ViewGroup)

        fun onBlockProjectNotificationsClick(hidden: Boolean, view : ViewGroup)
        fun onBlockOrganizationNotificationsClick(hidden: Boolean, view : ViewGroup)
        fun onBlockEventNotificationsClick(hidden: Boolean, view : ViewGroup)

        fun onDeleteProfileClick()
        fun onDeleteProfileConfirm()


        fun showChangeNameClick()
        fun showChangeShortNameClick()

        fun onBindVkAccount(context : Context, view: ViewGroup)
    }
}
