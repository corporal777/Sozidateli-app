package com.example.ui.user.edit

import android.graphics.Bitmap
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.user.User
import com.example.ui.base.BaseContract

interface UserEditContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setMainData(user: User, avatar: Bitmap?)

        @StateStrategyType(SkipStrategy::class)
        fun showDisabledMainInputInfo()

        @StateStrategyType(SkipStrategy::class)
        fun showTakePictureChooser()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun changeUserAvatar(avatar: Bitmap?)

        @StateStrategyType(SkipStrategy::class)
        fun showUpdateError(message: String? = null)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPersonalData(user: User)

        @StateStrategyType(SkipStrategy::class)
        fun showChangeEmail()

        @StateStrategyType(SkipStrategy::class)
        fun showChangeEmailComplete(email: String)

        @StateStrategyType(SkipStrategy::class)
        fun showChangePassword()

        @StateStrategyType(SkipStrategy::class)
        fun showPasswordChangeComplete()
    }

    interface Presenter : BaseContract.Presenter {
        //main data
        fun onDisabledMainInputInfoClick()

        fun onEditAvatarClick()
        fun onRemoveAvatarClick()
        fun onTakePhotoFromCameraRequest()
        fun onTakePhotoFromGalleryRequest()

        //personal data
        fun onChangeEmailClick()
        fun onChangeEmailConfirm(email: String)
        fun onChangePasswordClick()
        fun onChangePasswordClickConfirm(oldPassword: String, newPassword: String, newPasswordConfirm: String)

        fun onSaveClick(data: Map<String, Any?>)
        fun onCancelClick()
    }
}
