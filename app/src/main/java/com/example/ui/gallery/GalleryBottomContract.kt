package com.example.ui.gallery

import android.net.Uri
import android.widget.ImageView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ImageModel
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import io.reactivex.Single
import java.util.*

interface GalleryBottomContract {
    interface View : BaseBottomSheetContract.View {

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setGalleryImages(images : List<Uri>)

        @StateStrategyType(SkipStrategy::class)
        fun updateCameraPreviewItem()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideGalleryFragment()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPhotoUpdated(image : ImageModel?)

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