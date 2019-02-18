package com.example.ui.base.takePhoto

import android.net.Uri
import com.example.R
import com.example.ui.base.BasePresenter


abstract class TakePhotoPresenter<V : TakePhotoContract.View>
    : BasePresenter<V>(), TakePhotoContract.Presenter {


    private var isLoaded = false;

    override fun onPhotoFound(path: String?, uri: Uri?, rotation: Int): Boolean {
        if (!isLoaded) {
            checkPhotoPath(path, uri) { _, photoUri ->
                isLoaded = true
                viewState.startCrop(photoUri, rotation)
            }
        }
        return true
    }

    override fun onImageCropped(path: String?, uri: Uri?) {
        checkPhotoPath(path, uri) { photoPath, photoUri -> onImageTaken(photoPath, photoUri) }
    }

    private fun checkPhotoPath(path: String?, uri: Uri?, onSuccess: (photoPath: String, photoUri: Uri) -> Unit) {
        if (path == null || uri == null) showCanNotTakeImageToast()
        else onSuccess(path, uri)
    }

    override fun onPhotoFoundError(throwable: Throwable?) = showCanNotTakeImageToast()
    private fun showCanNotTakeImageToast() = viewState.showToast(R.string.error_take_image)

    override fun onTakePhotoRequest() {
        isLoaded = false
        viewState.showChangePhotoDialog()
    }

    override fun onTakePhotoFromCameraRequest() = viewState.showCamera()
    override fun onTakePhotoFromGalleryRequest() = viewState.showGallery()

    abstract fun onImageTaken(path: String, uri: Uri)
}
