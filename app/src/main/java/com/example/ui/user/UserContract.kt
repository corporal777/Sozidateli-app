package com.example.ui.user

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Organization
import com.example.data.models.ProfileUserData
import com.example.data.models.UserEditDataType
import com.example.data.models.user.RecommendationFile
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface UserContract {
    interface View : BaseContract.View {

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "user")
        fun setUser(profileUserData: ProfileUserData)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openChat(userName: String, userAvatar: String?, chatId: String)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "action")
        fun setActionSubscribe()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "action")
        fun setActionUnsubscribe()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "action")
        fun setActionUnblock()

        @StateStrategyType(SkipStrategy::class)
        fun downloadFile(file: String)

        @StateStrategyType(SkipStrategy::class)
        fun showOrganization(organization: Organization)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun showUserMenuButton(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUserMenu(isBlocked: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showBlockConfirmation()

        @StateStrategyType(SkipStrategy::class)
        fun showUpdateError(message: String? = null)

        @StateStrategyType(SkipStrategy::class)
        fun showStatus()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showDataEditor(type: UserEditDataType)
    }

    interface Presenter : BaseContract.Presenter {
        fun onWriteMessageClick()
        fun onOrganizationClick(organization: Organization)
        fun onFileClick(file: RecommendationFile)
        fun onMenuButtonUserClick()
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
    }
}
