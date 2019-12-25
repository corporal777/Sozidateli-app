package com.example.ui.user

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Organization
import com.example.data.models.ProfileUserData
import com.example.data.models.UserEditDataType
import com.example.data.models.user.RecommendationFile
import com.example.ui.base.BaseContract
import com.example.ui.views.UserSubscribeButton
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.OneExecutionByTagStateStrategy

interface UserContract {
    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUser(profileUserData: ProfileUserData)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openChat(userName: String, userAvatar: String?, chatId: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSubscribeAction(action: UserSubscribeButton.Action?)

        @StateStrategyType(SkipStrategy::class)
        fun downloadFile(file: String)

        @StateStrategyType(SkipStrategy::class)
        fun showOrganization(organization: Organization)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showBlockConfirmation()

        @StateStrategyType(SkipStrategy::class)
        fun showUpdateError(message: String? = null)

        @StateStrategyType(SkipStrategy::class)
        fun showStatus()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showDataEditor(type: UserEditDataType)

        @StateStrategyType(SkipStrategy::class)
        fun showChangePassword()

        @StateStrategyType(SkipStrategy::class)
        fun showPasswordChangeComplete()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "title")
        fun setNoTitle()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "title")
        fun setProfileTitle()
    }

    interface Presenter : BaseContract.Presenter {
        fun onWriteMessageClick()
        fun onOrganizationClick(organization: Organization)
        fun onFileClick(file: RecommendationFile)
        fun onStatusClick()

        fun onSubscribeClick()
        fun onUnsubscribeClick()
        fun onUnblockClick()
        fun onBlockClick()
        fun onBlockConfirm()

        fun onEditMainDataClick()
        fun onEditPersonalDataClick()
        fun onEditEducationClick()
        fun onEditWorkClick()
        fun onEditInterestsClick()
        fun onEditAdditionalNotesDataClick()
        fun onEditAdditionalFilesDataClick()

        fun onRefreshRequest()

        fun onChangePasswordClick()
        fun onChangePasswordClickConfirm(oldPassword: String, newPassword: String, newPasswordConfirm: String)
    }
}
