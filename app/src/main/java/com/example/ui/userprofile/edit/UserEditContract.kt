package com.example.ui.userprofile.edit

import com.example.data.models.*
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface UserEditContract {
    interface View : BaseContract.View {

        @AddToEndSingle
        fun setPlaceholder(type: UserEditDataType)

        @AddToEndSingle
        fun setPersonalData(user: UserDetail, state: String)

        @AddToEndSingle
        fun setContactsData(user: UserDetail)

        @AddToEndSingle
        fun setInterestsData(interests: Map<InterestNew, List<UserInterest>>)

        @OneExecution
        fun addUserFile(file: FileModel, fileCount: Int)

        @OneExecution
        fun deleteUserFile(file: FileModel, fileCount: Int)

        @OneExecution
        fun hideDeleteUserFile(file: FileModel)

        @OneExecution
        fun showChangeEmail()

        @OneExecution
        fun showPhoneConfirm(phone: String)

        @Skip
        fun updatePhoneConfirmation(phone: String)

        @OneExecution
        fun showEnterPassword(phone: String)

        @OneExecution
        fun hideEnterPassword()

        @OneExecution
        fun showFileSelector()

        @Skip
        fun downloadFile(file: String)

        @OneExecution
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

        @AddToEndSingle
        fun saveOnClick(saveOnClick: Boolean)

        @OneExecution
        fun showPhoneNotUnique(phone: String)

        @Skip
        fun showFileUploadLoading()

        @Skip
        fun hideFileUploadLoading()

        @Skip
        fun showUpdateError(message: String? = null)
    }

    interface Presenter : BaseContract.Presenter {
        //main data

        //personal data

        fun onChangeEmailClick()
        fun checkPhoneIsUnique(phone: String)
        fun checkPassword(password: String, phone: String)
        fun onShowPhoneConfirm(phone: String)

        //additional data

        fun onAddFileClick()
        fun onFilePicked(path: String, mimeType: String)
        fun onFileEditCancelClick()
        fun onFileClick(file: FileModel)
        fun onDeleteFilesClick(file: FileModel)

        fun onSaveContactsClick(data: MutableMap<String, Any?>)
        fun onSaveInterestsClick(data: List<InterestNew>)
        fun onNavigateUpRequest()

        fun onSavePersonalDataClick(data: List<FileModel>, d: Map<String, Any?>)
    }
}
