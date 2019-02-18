package com.example.ui.base.takePhoto

import android.net.Uri
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract
import com.example.util.photohelper.MediaUtils


interface TakePhotoContract {

    interface View : BaseContract.View {
        @StateStrategyType(SkipStrategy::class)
        fun startCrop(uri: Uri, rotation: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showChangePhotoDialog()

        @StateStrategyType(SkipStrategy::class)
        fun showCamera()

        @StateStrategyType(SkipStrategy::class)
        fun showGallery()
    }

    interface Presenter : MediaUtils.OnPhotoPathFoundListener {
        fun onTakePhotoRequest()
        fun onTakePhotoFromCameraRequest()
        fun onTakePhotoFromGalleryRequest()
        fun onImageCropped(path: String?, uri: Uri?)
    }
}
