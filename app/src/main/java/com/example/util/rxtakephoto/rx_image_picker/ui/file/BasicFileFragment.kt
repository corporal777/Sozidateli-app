package com.example.util.rxtakephoto.rx_image_picker.ui.file

import android.content.Intent
import android.net.Uri
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.util.rxtakephoto.rx_image_picker.entity.Result
import com.example.util.rxtakephoto.rx_image_picker.ui.BaseSystemPickerFragment
import com.example.util.rxtakephoto.rx_image_picker.ui.ICustomPickerConfiguration
import com.example.util.rxtakephoto.rx_image_picker.ui.gallery.DefaultSystemGalleryConfig
import com.example.util.rxtakephoto.rx_image_picker.ui.gallery.IGalleryCustomPickerView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class BasicFileFragment : BaseSystemPickerFragment(), IFileCustomPickerView {


    override fun display(
        fragmentActivity: FragmentActivity,
        @IdRes viewContainer: Int,
        configuration: ICustomPickerConfiguration?
    ) {

        val fragmentManager = fragmentActivity.supportFragmentManager
        val fragment: Fragment? = fragmentManager.findFragmentByTag(tag)
        val transaction = fragmentManager.beginTransaction()
        if (fragment != null) {
            transaction.remove(fragment)
        }
        if (viewContainer != 0) {
            transaction.add(viewContainer, this, tag)
        } else {
            transaction.add(this, tag)
        }
        transaction.commitAllowingStateLoss()
    }


    override fun pickImage(): Observable<Result> {
        publishSubject = PublishSubject.create<Result>()
        return uriObserver
    }

    override fun startRequest() {
        if (checkReadPermission()) {
            startPickImage()
        }
    }

    override fun startPickImage() {
        //val mimeTypes = arrayOf("image/*", "application/pdf")
        val mimeTypes = arrayOf("application/pdf")
        val intent = Intent().apply {
            type = "*/*"
            action = Intent.ACTION_GET_CONTENT
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        launchedImage.launch(intent)
    }

    override fun getActivityResultUri(data: Intent?): Uri? {
        return data?.data
    }
}