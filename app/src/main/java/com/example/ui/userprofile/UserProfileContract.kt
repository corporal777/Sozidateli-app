package com.example.ui.userprofile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.userprofile.base.BaseUserProfileContract

interface UserProfileContract {

    interface View : BaseUserProfileContract.View {

        @StateStrategyType(SkipStrategy::class)
        fun showTakePictureChooser(canRemove: Boolean, isBase: Boolean, isMax: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showMainData()

        @StateStrategyType(SkipStrategy::class)
        fun showContacts()

        @StateStrategyType(SkipStrategy::class)
        fun showInterests()

        @StateStrategyType(SkipStrategy::class)
        fun showEducation()

        @StateStrategyType(SkipStrategy::class)
        fun showExperience()

        @StateStrategyType(SkipStrategy::class)
        fun showEdit()
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
