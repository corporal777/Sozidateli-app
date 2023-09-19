package com.example.ui.gallery

import android.net.Uri
import android.widget.ImageView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import io.reactivex.Single
import java.util.*

interface GalleryBottomContract {
    interface View : BaseBottomSheetContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setGalleryImages(images : List<Uri>)

        @StateStrategyType(SkipStrategy::class)
        fun updateCameraPreviewItem()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideGalleryFragment()

        @StateStrategyType(SkipStrategy::class)
        fun showCameraActivity(uri: Uri?, imageView : ImageView)

        @StateStrategyType(SkipStrategy::class)
        fun showCropActivity(uri: Uri, imageView : ImageView?)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun onGalleryClick()
        fun onRemovePhotoClick()
        fun observeCropFinished(request : Single<Boolean>)
    }
}