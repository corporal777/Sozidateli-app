package com.example.ui.user.edit

import android.graphics.Bitmap
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Interest
import com.example.data.models.UserInterest
import com.example.data.models.user.RecommendationFile
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

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEducationData(user: User)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setWorkData(user: User)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setInterestsData(interests: Map<Interest, List<UserInterest>>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setAdditionalNotesData(user: User)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setAdditionalFilesData(user: User)

        @StateStrategyType(SkipStrategy::class)
        fun showFileSelector()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setFileEditData(file: RecommendationFile)

        @StateStrategyType(SkipStrategy::class)
        fun downloadFile(file: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun navigateUpChecked()
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

        //additional data

        fun onAddFileClick()
        fun onEditFileClick(file: RecommendationFile)
        fun onFilePicked(path: String)
        fun onFileEditSaveClick()
        fun onFileEditCancelClick()
        fun onFileClick(file: RecommendationFile)

        fun onSaveMainClick(data: Map<String, Any?>)
        fun onSavePersonalClick(data: Map<String, Any?>)
        fun onSaveEducationClick(data: Map<String, Any?>)
        fun onSaveWorkClick(data: Map<String, Any?>)
        fun onSaveInterestsClick(data: List<Interest>)
        fun onSaveAdditionalNotesClick(notes: String?)
        fun onSaveAdditionalFilesClick(data: Map<String, Any?>)
        fun onCancelClick()

        fun onNavigateUpRequest()
    }
}
