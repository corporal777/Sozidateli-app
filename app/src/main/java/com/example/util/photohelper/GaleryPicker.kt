package com.example.util.photohelper

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.qingmei2.rximagepicker.entity.Result
import com.qingmei2.rximagepicker.ui.BaseSystemPickerView
import com.qingmei2.rximagepicker.ui.ICustomPickerConfiguration
import com.qingmei2.rximagepicker.ui.IGalleryCustomPickerView
import com.qingmei2.rximagepicker.ui.gallery.SystemGalleryPickerView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class GaleryPicker : BaseSystemPickerView(), IGalleryCustomPickerView {

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
        if (!checkPermission()) {
            return
        }

        val pictureChooseIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        pictureChooseIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        pictureChooseIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        pictureChooseIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        pictureChooseIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        pictureChooseIntent.type = "image/*"

        startActivityForResult(pictureChooseIntent, BaseSystemPickerView.GALLERY_REQUEST_CODE)
    }

    override fun getActivityResultUri(data: Intent?): Uri? {
        return data?.data
    }

}