package com.example.ui.support.sendFile

import android.net.Uri
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SupportFile
import com.example.ui.base.bottomSheet.BaseBottomSheetContract

interface SupportFilesContract {
    interface View : BaseBottomSheetContract.View {

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setGalleryImages(images : List<Uri>)

        @StateStrategyType(SkipStrategy::class)
        fun updateCameraPreviewItem()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideGalleryFragment()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setFileUriReady(file : SupportFile)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun onOpenGalleryClick()
        fun onOpenCameraClick()
        fun onOpenFileClick()
        fun onOpenImageClick(uri : Uri?)
    }

}