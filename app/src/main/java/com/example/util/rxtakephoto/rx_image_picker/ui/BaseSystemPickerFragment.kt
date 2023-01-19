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
            var isGranted = false
           isGranted = permissions.entries.find { x -> !x.value }?.value == false
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
        if (!isAdded) {
            attachedSubject.subscribe { startRequest() }
        } else {
            startRequest()
        }
    }

    abstract fun startRequest()
    abstract fun startPickImage()

    abstract fun getActivityResultUri(data: Intent?): Uri?

    private fun onImagePicked(uri: Uri?) {
        if (uri != null) {
            publishSubject.onNext(parseResultNoExtraData(uri))
        }
        publishSubject.onComplete()
        closure()
    }

    private fun closure() {
        val fragmentTransaction = requireFragmentManager().beginTransaction()
        fragmentTransaction.remove(this)
        fragmentTransaction.commit()
    }

    protected fun checkPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
            }
            false
        } else {
            true
        }
    }

    protected fun checkWritePermission(): Boolean {
        var granted = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (readPermission != PackageManager.PERMISSION_GRANTED ||
                cameraPermission != PackageManager.PERMISSION_GRANTED) {
                launchedPermissions.launch(
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                )
                granted = false
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (writePermission != PackageManager.PERMISSION_GRANTED ||
                cameraPermission != PackageManager.PERMISSION_GRANTED) {
                launchedPermissions.launch(
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                )
                granted = false
            }
        } else granted = false
        return granted
    }

    protected fun checkReadPermission(): Boolean {
        return if (readPermission != PackageManager.PERMISSION_GRANTED) {
            launchedPermissions.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            false
        } else true
    }


    override fun onDestroy() {
        super.onDestroy()
        launchedPermissions.unregister()
        launchedImage.unregister()
    }

    private val readPermission by lazy {
        ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    private val writePermission by lazy {
        ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
    private val cameraPermission by lazy {
        ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
    }

    companion object {
        const val GALLERY_REQUEST_CODE = 100
        const val CAMERA_REQUEST_CODE = 101
    }
}