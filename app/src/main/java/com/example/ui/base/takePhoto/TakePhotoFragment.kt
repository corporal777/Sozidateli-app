package com.example.ui.base.takePhoto

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import bundleOf
import com.example.R
import com.example.ui.base.BaseFragment
import com.example.util.photohelper.CropActivity
import com.example.util.photohelper.MediaUtils
import com.example.util.photohelper.MediaUtils.ACTION_CROP


abstract class TakePhotoFragment<V : TakePhotoContract.View, P : TakePhotoPresenter<V>>
    : BaseFragment(), TakePhotoContract.View {

    open lateinit var presenter: P


    private val mediaUtils by lazy { MediaUtils(this, presenter) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun showCamera() = mediaUtils.requestImageFromCamera()

    override fun showGallery() = mediaUtils.requestImageFromGallery()

    override fun startCrop(uri: Uri, rotation: Int) {
        activity?.runOnUiThread { startActivityForResult(CropActivity.getStartIntent(context!!, uri, rotation), ACTION_CROP) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val uri = CropActivity.getUriFromResult(data)
        presenter.onImageCropped(mediaUtils.getPathFromUri(uri), uri)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        mediaUtils.handleOnPermissionResult(requestCode, permissions, grantResults)
    }

    override fun showChangePhotoDialog() {
        val alertDialog = AlertDialog.Builder(context!!)
                .setTitle(R.string.photo_alert_title)
                .setPositiveButton(R.string.photo_alert_gallery, DialogInterface.OnClickListener { dialogInterface, i ->
                    presenter.onTakePhotoFromGalleryRequest()
                    return@OnClickListener
                }).setNegativeButton(R.string.photo_alert_camera, DialogInterface.OnClickListener { dialogInterface, i ->
                    presenter.onTakePhotoFromCameraRequest()
                    return@OnClickListener
                }).create()
        alertDialog.show()
        /*val view = LayoutInflater.from(context!!).inflate(R.layout.bottom_sheet_select_photo, null, false)
        BottomSheetDialog(context!!).apply {
            setContentView(view)
            view.btnCamera.setOnClickListener {
                presenter.onTakePhotoFromCameraRequest()
                dismiss()
            }
            view.btnPhotos.setOnClickListener {
                presenter.onTakePhotoFromGalleryRequest()
                dismiss()
            }
        }
                .show()*/
    }

    override fun isShowToolbar() = true
}
