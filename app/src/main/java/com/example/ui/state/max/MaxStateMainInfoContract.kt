package com.example.ui.state.max

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.FileModel
import com.example.data.models.ImageModel
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract

interface MaxStateMainInfoContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPersonalData(user: UserDetail)

        @StateStrategyType(SkipStrategy::class)
        fun goToNext()

        @StateStrategyType(SkipStrategy::class)
        fun showUpdateError(message: String? = null)

        @StateStrategyType(SkipStrategy::class)
        fun showChangeEmail()

        @StateStrategyType(SkipStrategy::class)
        fun showChangeEmailComplete(email: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateFilesList(files: List<FileModel>?)

        @StateStrategyType(SkipStrategy::class)
        fun showFileSelector()

        @StateStrategyType(SkipStrategy::class)
        fun downloadFile(file: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setFileEditData(file: FileModel)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun saveOnClick(saveOnClick: Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun photoUpdated(photo: ImageModel)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setClickClose(type : Int)
    }
    interface Presenter : BaseContract.Presenter, BaseContract.OnChangeElevation {
        fun onClickClose()
        fun updateFiles(data: MutableList<FileModel>, d: MutableMap<String, Any?>)
        fun updateFiles(data: MutableMap<String, Any?>)
        fun onChangeEmailClick()
        fun onChangeEmailConfirm(email: String, isFirst: Boolean)

        fun onFilePicked(path: String, mimeType: String)
        fun onAddFileClick()
        fun onFileClick(file: FileModel)
        fun onEditFileClick(file: FileModel)
        fun onDeleteFilesClick(data: FileModel)
        fun onSaveFileClick(data: MutableMap<String, Any?>)

        fun onTakePhotoFromGalleryClick()
        fun onTakePhotoFromCameraClick()
        fun onRemovePhotoClick()
    }
}