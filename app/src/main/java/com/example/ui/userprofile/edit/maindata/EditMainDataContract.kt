package com.example.ui.userprofile.edit.maindata

import com.example.data.models.*
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface EditMainDataContract {
    interface View : BaseContract.View {

        @OneExecution
        fun setPlaceholder()

        @OneExecution
        fun setPersonalData(user: UserDetail, state: String)

        @Skip
        fun addUserFile(file: FileModel, fileCount: Int)

        @Skip
        fun deleteUserFile(file: FileModel, fileCount: Int)

        @Skip
        fun hideDeleteUserFile(file: FileModel)

        @Skip
        fun showFileSelector()

        @Skip
        fun downloadFile(file: String)

        @Skip
        fun showFileUploadLoading()

        @Skip
        fun hideFileUploadLoading()
    }

    interface Presenter : BaseContract.Presenter {
        fun onAddFileClick()
        fun onFilePicked(path: String, mimeType: String)
        fun onFileClick(file: FileModel)
        fun onDeleteFilesClick(file: FileModel)

        fun onSavePersonalDataClick(data: List<FileModel>, d: Map<String, Any?>)
    }
}
