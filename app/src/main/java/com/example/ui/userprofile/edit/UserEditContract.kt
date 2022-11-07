package com.example.ui.userprofile.edit

import android.graphics.Bitmap
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface UserEditContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setMainData(user: UserDetail, avatar: Bitmap?)

        @StateStrategyType(SkipStrategy::class)
        fun showDisabledMainInputInfo()

        @StateStrategyType(SkipStrategy::class)
        fun showTakePictureChooser()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun changeUserAvatar(avatar: Bitmap?)

        @StateStrategyType(SkipStrategy::class)
        fun showUpdateError(message: String? = null)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPersonalData(user: UserDetail, state: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setContactsData(user: UserDetail)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPhoneData(user: UserDetail)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateFilesList(files: List<FileModel>?)

        @StateStrategyType(SkipStrategy::class)
        fun showChangeEmail()

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneConfirm(phone: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEducationData(user: UserDetail)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setWorkData(user: UserDetail)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setInterestsData(interests: Map<InterestNew, List<UserInterest>>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setAdditionalNotesData(user: UserDetail)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setAdditionalFilesData(user: UserDetail)

        @StateStrategyType(SkipStrategy::class)
        fun showFileSelector()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setFileEditData(file: FileModel)

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

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneNotUnique(phone: String)

        @StateStrategyType(SkipStrategy::class)
        fun codeSuccess()

        @StateStrategyType(SkipStrategy::class)
        fun setTimerForResendCode(seconds : Int)
    }

    interface Presenter : BaseContract.Presenter, BaseContract.OnChangeElevation {
        //main data

        fun onDisabledMainInputInfoClick()
        fun onEditAvatarClick()
        fun onRemoveAvatarClick()
        fun onTakePhotoFromCameraRequest()
        fun onTakePhotoFromGalleryRequest()

        //personal data

        fun onChangeEmailClick()
        fun onConfirmPhoneClick(phone: String)

        //additional data

        fun onAddFileClick()
        fun onEditFileClick(file: FileModel)
        fun onFilePicked(path: String, mimeType: String)
        fun onFileEditSaveClick()
        fun onFileEditCancelClick()
        fun onFileClick(file: FileModel)

        fun onSaveMainClick(data: MutableMap<String, Any?>)
        fun onSavePersonalClick(data: MutableMap<String, Any?>)
        fun onSaveContactsClick(data: MutableMap<String, Any?>)
        fun onSaveEducationClick(educationLevel: ToggleIntModel?, educationsList: List<EducationModel>?, degree: List<AcademicDegreeModel>?)
        fun onSaveWorkClick(data: WorkExperienceServerModel)
        fun onSaveInterestsClick(data: List<InterestNew>)
        fun onSaveAdditionalNotesClick(notes: String?)
        fun onSaveAdditionalFilesClick(data: MutableMap<String, Any?>)
        fun onCancelClick()

        fun onNavigateUpRequest()
        fun onSaveFileClick(data: MutableMap<String, Any?>)
        fun onDeleteFilesClick(data: FileModel)
        fun updateFiles(data: MutableList<FileModel>, d: MutableMap<String, Any?>)
    }
}
