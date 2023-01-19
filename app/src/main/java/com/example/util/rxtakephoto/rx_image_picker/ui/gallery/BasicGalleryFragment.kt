package com.example.util.rxtakephoto.rx_image_picker.ui.gallery

import android.content.Intent
import android.net.Uri
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.util.rxtakephoto.rx_image_picker.ui.BaseSystemPickerFragment
import com.example.util.rxtakephoto.rx_image_picker.ui.ICustomPickerConfiguration
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import com.example.util.rxtakephoto.rx_image_picker.entity.Result

class BasicGalleryFragment : BaseSystemPickerFragment(), IGalleryCustomPickerView {

    private var mDefaultSystemGalleryConfig: DefaultSystemGalleryConfig? = null

    override fun display(fragmentActivity: FragmentActivity,
                         @IdRes viewContainer: Int,
                         configuration: ICustomPickerConfiguration?) {
        injectConfigurations(configuration)

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

    private fun injectConfigurations(configuration: ICustomPickerConfiguration?) {
        when (configuration) {
            is DefaultSystemGalleryConfig ->
                this.mDefaultSystemGalleryConfig = configuration
            else -> {
                // do nothing
            }
        }
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
        val mTemp = mDefaultSystemGalleryConfig ?: DefaultSystemGalleryConfig.defaultInstance()
        mTemp.apply {
            launchedImage.launch(this.systemIntent())
        }
    }

    override fun getActivityResultUri(data: Intent?): Uri? {
        return data?.data
    }
}