package com.example.ui.user

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.data.models.user.RecommendationFile
import com.example.ui.base.BaseContract
import com.example.ui.views.UserSubscribeButton
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.OneExecutionByTagStateStrategy

interface UserContract {
    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showShimmerPlaceholder()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUser(profileUserData: ProfileUserData)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openChat(userName: String, userAvatar: String?, chatId: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSubscribeFavoriteAction(action: UserSubscribeButton.Action?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEnableAddToFavoriteButton(enabled : Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSubscribeBlockAction(action: UserSubscribeButton.Action?)

        @StateStrategyType(SkipStrategy::class)
        fun downloadFile(file: String)

        @StateStrategyType(SkipStrategy::class)
        fun showOrganization(organization: /*Organization*/OrganizationNew)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showBlockConfirmation()

        @StateStrategyType(SkipStrategy::class)
        fun showUpdateError(message: String? = null)

        @StateStrategyType(SkipStrategy::class)
        fun showUserHiddenDialog()

        @StateStrategyType(SkipStrategy::class)
        fun setAppBarShadow(value : Float)
    }

    interface Presenter : BaseContract.Presenter, BaseContract.OnChangeElevation {
        fun onWriteMessageClick()
        fun onOrganizationClick(organization: /*Organization*/OrganizationNew)
        fun onFileClick(file: /*RecommendationFile*/FileModel)

        fun onSubscribeClick()
        fun onUnsubscribeClick()
        fun onUnblockClick()
        fun onBlockClick()
        fun onBlockConfirm()

        fun onRefreshRequest()
    }
}
