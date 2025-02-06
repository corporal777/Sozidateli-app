package com.example.ui.gallery

import android.net.Uri
import android.widget.ImageView
import com.example.data.models.ImageModel
import com.example.ui.base.bottomSheet.BaseBSContract
import io.reactivex.Single
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface GalleryBottomContract {
    interface View : BaseBSContract.View {

        @OneExecution
        fun setGalleryImages(images : List<Uri>)

        @Skip
        fun updateCameraPreviewItem()

        @OneExecution
        fun setPhotoUpdated(image : ImageModel?)

        @Skip
        fun showCameraActivity(uri: Uri?, imageView : ImageView)

        @Skip
        fun showCropActivity(uri: Uri, imageView : ImageView?)
    }

    interface Presenter : BaseBSContract.Presenter {
        fun onGalleryClick()
        fun onRemovePhotoClick()
        fun observeCropFinished(request : Single<Boolean>)
    }
}