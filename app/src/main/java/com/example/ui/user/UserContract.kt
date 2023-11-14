package com.example.ui.user

import com.example.data.models.FileModel
import com.example.data.models.OrganizationNew
import com.example.data.models.ProfileUserData
import com.example.ui.base.BaseContract
import com.example.ui.views.UserSubscribeButton
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface UserContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setUser(profileUserData: ProfileUserData)

        @OneExecution
        fun openChat(userName: String, userAvatar: String?, chatId: String)

        @OneExecution
        fun setSubscribeFavoriteAction(action: UserSubscribeButton.Action?)

        @OneExecution
        fun setEnableAddToFavoriteButton(enabled : Boolean)

        @OneExecution
        fun setSubscribeBlockAction(action: UserSubscribeButton.Action?)

        @Skip
        fun downloadFile(file: String)

        @Skip
        fun showOrganization(organization: OrganizationNew)

        @OneExecution
        fun showBlockConfirmation()

        @Skip
        fun showUpdateError(message: String? = null)

        @Skip
        fun showUserHiddenDialog()
    }

    interface Presenter : BaseContract.Presenter{
        fun onWriteMessageClick()
        fun onOrganizationClick(organization: OrganizationNew)
        fun onFileClick(file: FileModel)

        fun onSubscribeClick()
        fun onUnsubscribeClick()
        fun onUnblockClick()
        fun onBlockClick()
        fun onBlockConfirm()

        fun onRefreshRequest()
    }
}
