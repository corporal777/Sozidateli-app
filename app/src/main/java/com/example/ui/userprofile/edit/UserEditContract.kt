package com.example.ui.userprofile.edit

import android.graphics.Bitmap
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Interest
import com.example.data.models.UserInterest
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

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

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPersonalDataNew(user: User)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setContactsData(user: User)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPhoneData(user: User)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateFilesList(files: List<RecommendationFile>?)

        @StateStrategyType(SkipStrategy::class)
        fun showChangeEmail()

        @StateStrategyType(SkipStrategy::class)
        fun showChangeEmailComplete(email: String)

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneConfirm(phone: String)

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

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "title")
        fun setMainTitle()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "title")
        fun setPersonalTitle()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "title")
        fun setContactsTitle()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "title")
        fun setPhoneTitle()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "title")
        fun setEducationTitle()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "title")
        fun setWorkTitle()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "title")
        fun setInterestsTitle()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "title")
        fun setAdditionalNotesTitle()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "title")
        fun setAdditionalFilesTitle()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun saveOnClick(saveOnClick: Boolean)
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
        fun onConfirmPhoneClick(phone: String)

        //additional data

        fun onAddFileClick()
        fun onEditFileClick(file: RecommendationFile)
        fun onFilePicked(path: String)
        fun onFileEditSaveClick()
        fun onFileEditCancelClick()
        fun onFileClick(file: RecommendationFile)

        fun onSaveMainClick(data: Map<String, Any?>)
        fun onSavePersonalClick(data: Map<String, Any?>)
        fun onSaveContactsClick(data: Map<String, Any?>)
        fun onSaveEducationClick(data: Map<String, Any?>)
        fun onSaveWorkClick(data: Map<String, Any?>)
        fun onSaveInterestsClick(data: List<Interest>)
        fun onSaveAdditionalNotesClick(notes: String?)
        fun onSaveAdditionalFilesClick(data: Map<String, Any?>)
        fun onCancelClick()

        fun onNavigateUpRequest()
    }
}
