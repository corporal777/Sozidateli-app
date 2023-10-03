package com.example.util.rxtakephoto

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.FragmentActivity
import com.example.ui.gallery.cropImage.cropHelper.CropImageView
import com.example.util.rxtakephoto.CropActivity.Companion.CROP_MODE_DEFAULT
import com.example.util.rxtakephoto.rx_image_picker.core.RxImagePicker
import com.example.util.rxtakephoto.rx_image_picker.entity.Result
import com.example.util.saveImageToGallery
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.io.IOException


class RxTakePhoto(
    private val context: FragmentActivity
) {

    private val rxPermissions = RxPermissions(context)
    private val rxImagePicker = RxImagePicker.create()

    fun takeCameraImage(): Observable<ResultRotation> {
        return rxImagePicker.openCamera(context)
            .findRotation()
    }

    fun takeGalleryImage(): Observable<ResultRotation> {
        return rxImagePicker
            .openGallery(context)
            .findRotation()
    }

    fun saveImage(image: Bitmap): Completable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Completable.fromAction { saveImageToGallery(context, image, "sozidateli_images") }
        } else {
            rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .flatMapCompletable { granted ->
                    if (granted) {
                        Completable.fromAction {
                            saveImageToGallery(context, image, "sozidateli_images")
                        }
                    } else Completable.error(PermissionNotGrantedException())
                }
        }
    }

    fun crop(
        resultRotation: ResultRotation,
        outputMaxWidth: Int = 0,
        outputMaxHeight: Int = 0,
        outputQuality: Int = 0,
        cropMode: CropImageView.CropMode = CROP_MODE_DEFAULT
    ): Single<Bitmap> {
        return crop(
            resultRotation.uri,
            resultRotation.rotation,
            outputMaxWidth,
            outputMaxHeight,
            outputQuality,
            cropMode
        )
    }

    private fun crop(
        uri: Uri,
        rotation: Int = 0,
        outputMaxWidth: Int = 0,
        outputMaxHeight: Int = 0,
        outputQuality: Int = 0,
        cropMode: CropImageView.CropMode = CROP_MODE_DEFAULT
    ): Single<Bitmap> {
        context.startActivity(
            CropActivity.getStartIntent(
                context,
                uri,
                rotation,
                outputMaxWidth,
                outputMaxHeight,
                outputQuality,
                cropMode
            )
        )
        return CropCallbackHelper.createRequest(uri.toString())
    }

    private fun Observable<Result>.findRotation(): Observable<ResultRotation> {
        return this.map {
            val uri = it.uri
            ResultRotation(uri, findImageRotation(uri))
        }
    }

    private fun findImageRotation(uri: Uri): Int {
        return try {
            with(context.contentResolver.openInputStream(uri)!!) {
                val exifInterface = ExifInterface(this)

                var rotation = 0
                val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotation = 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotation = 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotation = 270
                }

                rotation
            }
        } catch (ignored: IOException) {
            0
        }
    }
}