package com.example.ui.user

import android.graphics.Bitmap
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Organization
import com.example.data.models.ProfileUserData
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
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
        fun setMainDataEditMode(user: User, avatar: Bitmap?, edit: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showTakePictureChooser()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun changeUserAvatar(avatar: Bitmap?)

        @StateStrategyType(SkipStrategy::class)
        fun showDisabledMainInputInfo()

        @StateStrategyType(SkipStrategy::class)
        fun setPersonalDataDataEditMode(user: User, edit: Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onWriteMessageClick()
        fun onOrganizationClick(organization: Organization)
        fun onFileClick(file: RecommendationFile)
        fun onMenuButtonUserClick()

        fun onSubscribeClick()
        fun onUnsubscribeClick()
        fun onUnblockClick()
        fun onBlockClick()
        fun onBlockConfirm()

        fun onEditMainDataClick()
        fun onEditMainDataCancelClick()
        fun onEditMainSaveClick(data: Map<String, Any?>)
        fun onDisabledMainInputInfoClick()
        fun onEditAvatarClick()
        fun onRemoveAvatarClick()
        fun onTakePhotoFromCameraRequest()
        fun onTakePhotoFromGalleryRequest()

        fun onEditPersonalDataClick()
        fun onEditPersonalDataCancelClick()
        fun onEditPersonalDataSaveClick(data: Map<String, Any?>)
    }
}
