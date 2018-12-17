package com.example.ui.image

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import javax.inject.Inject

@InjectViewState
class ImageViewPresenter
@Inject constructor(
) : MvpPresenter<ImageViewContract.View>(), ImageViewContract.Presenter {

    var url: String? = null
    @DrawableRes
    var resource: Int? = null
    var bitmap: Bitmap? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val url = this.url
        val resource = this.resource
        val bitmap = this.bitmap
        viewState.apply {
            when {
                url != null -> findImageBitmap(url)
                resource != null -> findImageBitmap(resource)
                bitmap != null -> onBitmapFound(bitmap)
                else -> throw NullPointerException("All supported image types are null")
            }
        }
    }

    override fun onBitmapFound(bitmap: Bitmap) {
        this.bitmap = bitmap
        viewState.setImage(bitmap)
    }

    override fun onBitmapFoundFailed(t: Exception) {
        viewState.apply { showError() }
    }
}
