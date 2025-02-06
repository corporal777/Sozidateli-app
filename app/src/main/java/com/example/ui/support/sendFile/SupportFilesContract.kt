package com.example.ui.support.sendFile

import android.net.Uri
import com.example.data.models.SupportFile
import com.example.ui.base.bottomSheet.BaseBSContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface SupportFilesContract {
    interface View : BaseBSContract.View {

        @OneExecution
        fun setGalleryImages(images : List<Uri>)

        @Skip
        fun updateCameraPreviewItem()

        @OneExecution
        fun setFileUriReady(file : SupportFile)
    }

    interface Presenter : BaseBSContract.Presenter {
        fun onOpenGalleryClick()
        fun onOpenCameraClick()
        fun onOpenFileClick()
        fun onOpenImageClick(uri : Uri?)
    }

}