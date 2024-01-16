package com.example.util.rxtakephoto.rx_image_picker.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.util.rxtakephoto.PermissionNotGrantedException
import com.example.util.rxtakephoto.rx_image_picker.entity.Result
import com.example.util.rxtakephoto.rx_image_picker.function.parseResultNoExtraData
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


abstract class BaseSystemPickerFragment : Fragment() {

    private val attachedSubject = PublishSubject.create<Boolean>()

    protected var publishSubject: PublishSubject<Result> = PublishSubject.create<Result>()

    private val canceledSubject: PublishSubject<Int> = PublishSubject.create<Int>()

    val uriObserver: Observable<Result>
        get() {
            requestPickImage()
            return publishSubject.takeUntil(canceledSubject)
        }

    val launchedImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { uri ->
            if (uri.resultCode == RESULT_OK) onImagePicked(getActivityResultUri(uri.data))
            else canceledSubject.onNext(0)
        }

    private val launchedPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGranted: Boolean = permissions.entries.find { x -> !x.value }?.value == false
            if (!isGranted) startPickImage()
            else {
                publishSubject.onError(PermissionNotGrantedException())
                publishSubject.onComplete()
                closure()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        attachedSubject.onNext(true)
        attachedSubject.onComplete()
    }

    private fun requestPickImage() {
        if (!isAdded) attachedSubject.subscribe { startRequest() }
        else startRequest()
    }

    abstract fun startRequest()
    abstract fun startPickImage()

    abstract fun getActivityResultUri(data: Intent?): Uri?

    private fun onImagePicked(uri: Uri?) {
        if (uri != null) publishSubject.onNext(parseResultNoExtraData(uri))
        publishSubject.onComplete()
        closure()
    }

    private fun closure() {
        val fragmentTransaction = requireFragmentManager().beginTransaction()
        fragmentTransaction.remove(this)
        fragmentTransaction.commit()
    }


    protected fun checkCameraPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!cameraPermissionIsGranted) {
                launchedPermissions.launch(arrayOf(Manifest.permission.CAMERA))
                false
            } else true
        } else {
            if (!writePermissionIsGranted || !cameraPermissionIsGranted) {
                launchedPermissions.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                false
            } else true
        }
    }

    protected fun checkReadPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (readMedia != PackageManager.PERMISSION_GRANTED) {
                launchedPermissions.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
                false
            } else true
        } else {
            if (readPermission != PackageManager.PERMISSION_GRANTED) {
                launchedPermissions.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                false
            } else true
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        launchedPermissions.unregister()
        launchedImage.unregister()
    }

    private val readPermission by lazy {
        ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private val readMedia by lazy {
        ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_MEDIA_IMAGES
        )
    }

    private val writePermissionIsGranted by lazy {
        ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
    }
    private val cameraPermissionIsGranted by lazy {
        ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val GALLERY_REQUEST_CODE = 100
        const val CAMERA_REQUEST_CODE = 101
    }
}