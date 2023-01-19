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

        @StateStrategyType(SkipStrategy::class)
        fun showUpdateError(message: String? = null)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPersonalData(user: UserDetail, state: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setContactsData(user: UserDetail)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun addNewUserFile(file: FileModel)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun deleteUserFile(file: FileModel)

        @StateStrategyType(SkipStrategy::class)
        fun showChangeEmail()

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneConfirm(phone: String)

        @StateStrategyType(SkipStrategy::class)
        fun updatePhoneConfirmation(phone: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEnterPassword(phone: String)

        @StateStrategyType(SkipStrategy::class)
        fun hideEnterPassword()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setInterestsData(interests: Map<InterestNew, List<UserInterest>>)

        @StateStrategyType(SkipStrategy::class)
        fun showFileSelector()

        @StateStrategyType(SkipStrategy::class)
        fun downloadFile(file: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun navigateUpChecked()


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


        @StateStrategyType(AddToEndSingleStrategy::class)
        fun saveOnClick(saveOnClick: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneNotUnique(phone: String)
    }

    interface Presenter : BaseContract.Presenter, BaseContract.OnChangeElevation {
        //main data

        //personal data

        fun onChangeEmailClick()
        fun checkPhoneIsUnique(phone: String)
        fun checkPassword(password : String, phone: String)
        fun onShowPhoneConfirm(phone: String)

        //additional data

        fun onAddFileClick()
        fun onFilePicked(path: String, mimeType: String)
        fun onFileEditCancelClick()
        fun onFileClick(file: FileModel)

        fun onSaveContactsClick(data: MutableMap<String, Any?>)
        fun onSaveInterestsClick(data: List<InterestNew>)
        fun onNavigateUpRequest()
        fun onDeleteFilesClick(data: FileModel)
        fun updateFiles(data: MutableList<FileModel>, d: MutableMap<String, Any?>)
    }
}
