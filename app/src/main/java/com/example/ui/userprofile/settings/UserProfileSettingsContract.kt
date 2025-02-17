package com.example.ui.userprofile.settings

import android.content.Context
import android.view.ViewGroup
import com.example.data.models.FieldDetails
import com.example.data.models.SnAuth
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import com.example.ui.userprofile.base.BaseUserProfileContract
import com.example.ui.views.CustomCheckView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface UserProfileSettingsContract {
    interface View : BaseContract.View {

        @OneExecution
        fun setUserData(user: UserDetail)

        @OneExecution
        fun showChangeEmail(email : String?)

        @OneExecution
        fun showChangePhone()

        @OneExecution
        fun showChangePassword()

        @OneExecution
        fun showCreatePassword()

        @OneExecution
        fun showChangeName(user : UserDetail)

        @OneExecution
        fun showChangeShortName(user : UserDetail)

        @OneExecution
        fun showDeleteProfile()

        @Skip
        fun showBlockingLoading(show : Boolean)

        @Skip
        fun showAccountAlreadyBoundDialog(snAuth: SnAuth?)
    }

    interface Presenter : BaseContract.Presenter {
        fun onChangePhoneClick()
        fun onChangePasswordClick()
        fun onChangeEmailClick()

        fun onChangePrivacyConfirm(hidden: Boolean)

        fun onBlockProjectNotificationsClick(hidden: Boolean)
        fun onBlockOrganizationNotificationsClick(hidden: Boolean)
        fun onBlockEventNotificationsClick(hidden: Boolean)

        fun onDeleteProfileClick()
        fun onDeleteProfileConfirm()


        fun showChangeNameClick()
        fun showChangeShortNameClick()

        fun onBindVkAccount(context : Context, snAuth: SnAuth?)
        fun onUnbindVkAccount()
    }
}
