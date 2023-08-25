package com.example.util.rxtakephoto.rx_image_picker.ui.camera

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.IdRes
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.BuildConfig
import com.example.util.rxtakephoto.rx_image_picker.ui.BaseSystemPickerFragment
import com.example.util.rxtakephoto.rx_image_picker.ui.ICustomPickerConfiguration
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.text.SimpleDateFormat
import com.example.util.rxtakephoto.rx_image_picker.entity.Result
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class BasicCameraFragment : BaseSystemPickerFragment(), ICameraCustomPickerView {

    private var cameraPictureUrl: Uri? = null

    override fun display(fragmentActivity: FragmentActivity,
                         @IdRes viewContainer: Int,
                         configuration: ICustomPickerConfiguration?) {
        val fragmentManager = fragmentActivity.supportFragmentManager
        val fragment: Fragment? = fragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            val transaction = fragmentManager.beginTransaction()
            if (viewContainer != 0) {
                transaction.add(viewContainer, this, tag)
            } else {
                transaction.add(this, tag)
            }
            transaction.commitAllowingStateLoss()
        }
    }

    override fun pickImage(): Observable<Result> {
        publishSubject = PublishSubject.create<Result>()
        return uriObserver
    }

    override fun startRequest() {
        if (checkWritePermission()) {
            startPickImage()
        }
    }

    override fun startPickImage() {
        //cameraPictureUrl = createImageUri()
        cameraPictureUrl = createImageCacheUri()
        val pictureChooseIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        pictureChooseIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPictureUrl)
        launchedImage.launch(pictureChooseIntent)
    }

    override fun getActivityResultUri(data: Intent?): Uri? {
        return cameraPictureUrl
    }

    private fun createImageUri(): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val contentResolver = requireContext().contentResolver
        val cv = ContentValues()
        cv.put(MediaStore.Images.Media.TITLE, timeStamp)
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
    }

    private fun createImageCacheUri(): Uri? {
        val imagesFolder = File(requireContext().cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "camera_image.png")
            uri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return uri
    }
}