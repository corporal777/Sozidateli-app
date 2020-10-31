package com.example.ui.userprofile

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.userprofile.base.BaseUserProfileContract

interface UserProfileContract {
    interface View : BaseUserProfileContract.View {

        @StateStrategyType(SkipStrategy::class)
        fun showTakePictureChooser(canRemove: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showMainData()
    }

    interface Presenter : BaseUserProfileContract.Presenter {

        fun onEditAvatarClick()
        fun onMainDataClick()
        fun onContactsClick()
        fun onInterestsClick()
        fun onEducationClick()
        fun onExperienceClick()

        fun onTakePhotoFromGalleryClick()
        fun onTakePhotoFromCameraClick()
        fun onRemovePhotoClick()
    }
}
