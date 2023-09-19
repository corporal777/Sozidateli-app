package com.example.ui.gallery.camera

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import coil.load
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.ActivityCustomCameraBinding
import com.example.databinding.ActivityImageCropBinding
import com.example.ui.base.BaseCustomActivity
import com.example.ui.base.MvpAppCompatActivity
import com.example.ui.gallery.cropImage.CropImageActivity
import com.example.util.rxtakephoto.CropActivity
import com.google.common.util.concurrent.ListenableFuture
import dagger.android.AndroidInjection
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Provider

class CustomCameraActivity : BaseCustomActivity<ActivityCustomCameraBinding>(), CustomCameraContract.View {

    override fun getViewBinding() = ActivityCustomCameraBinding.inflate(layoutInflater)

    @InjectPresenter
    lateinit var presenter: CustomCameraPresenter

    @Inject
    lateinit var presenterProvider: Provider<CustomCameraPresenter>

    @ProvidePresenter
    fun providePresenter(): CustomCameraPresenter = presenterProvider.get()

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
        ProcessCameraProvider.getInstance(this)
    private lateinit var cameraProvider: ProcessCameraProvider

    private var cameraType = CameraSelector.DEFAULT_BACK_CAMERA
    private var isFlashEnabled = false
    private lateinit var camera: Camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.extras?.let { CustomCameraActivityArgs.fromBundle(it) }?.apply {
            presenter.imageUrl = url
            presenter.customTransitionName = transitionName
        }

        mBinding.ivCapture.setOnClickListener { takePhoto() }
        mBinding.ivSwitchCamera.setOnClickListener {
            cameraType =
                if (cameraType == DEFAULT_BACK_CAMERA) DEFAULT_FRONT_CAMERA
                else DEFAULT_BACK_CAMERA
            startCameraPreview()

            mBinding.apply {
                isFlashEnabled = false
                ivFlash.setImageResource(R.drawable.ic_flash)
            }
        }
        mBinding.ivFlash.setOnClickListener {
            isFlashEnabled = !isFlashEnabled
            if (this::camera.isInitialized) {
                camera.cameraControl.enableTorch(isFlashEnabled)
                mBinding.apply {
                    if (isFlashEnabled) ivFlash.setImageResource(R.drawable.ic_no_flash)
                    else ivFlash.setImageResource(R.drawable.ic_flash)
                }
            }
        }
        mBinding.ivSave.setOnClickListener {
            showCropActivity()
            finish()
        }
        mBinding.ivClose.setOnClickListener {
            finish()
        }
        mBinding.ivRemove.setOnClickListener { startCameraPreview() }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun setImage(uri: Uri?) {
        mBinding.imageView.load(uri)
        startPostponedEnterTransition()
        presenter.onStartPreview()
    }

    override fun setCustomTransitionName(transitionName: String) {
        mBinding.imageView.transitionName = transitionName
    }

    override fun setDefaultTransitionName() {
        mBinding.imageView.transitionName = getString(R.string.camera_transition_name)
    }

    override fun startCameraPreview() {
        mBinding.apply {
            rlButtons.isInvisible = false
            rlDone.isInvisible = true
        }
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(mBinding.viewFinder.surfaceProvider) }
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this, cameraType, preview, imageCapture)
                val isHasFlash = camera.cameraInfo.hasFlashUnit()
                mBinding.ivFlash.isVisible = isHasFlash

                mBinding.viewFinder.isInvisible = false
                mBinding.imageView.isInvisible = true
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))

    }

    override fun stopCameraPreview() {
        cameraProvider.unbindAll()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = getOutputCacheFilePicture()
        val metadata = ImageCapture.Metadata().apply {
            isReversedHorizontal = cameraType == DEFAULT_FRONT_CAMERA
        }
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .setMetadata(metadata)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    exc.printStackTrace()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    setCapturedImage(savedUri)
                }
            })
    }

    override fun setCapturedImage(uri: Uri) {
        mBinding.apply {
            rlButtons.isInvisible = true
            rlDone.isInvisible = false
        }
        mBinding.imageView.apply {
            load(uri)
            isInvisible = false
        }
        mBinding.viewFinder.apply {
            isInvisible = true
            stopCameraPreview()
        }
        presenter.capturedImageUri = uri
    }

    private fun showCropActivity() {
        val intent = Intent(this, CropImageActivity::class.java).apply {
            putExtras(
                bundleOf(
                    CropActivity.ARG_URL to presenter.capturedImageUri.toString(),
                    CropActivity.ARG_TRANSITION_NAME to null
                )
            )
        }
        startActivity(intent)
    }

    private fun getOutputDirectory(): File {
        val imagesFolder = File(cacheDir, "images")
        return try {
            imagesFolder.mkdirs()
            File(imagesFolder, "captured_image.png")
        } catch (e: IOException) {
            val mediaDir = externalMediaDirs.firstOrNull()?.let {
                File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
        }
    }

    private fun getOutputCacheFilePicture(): File {
        val tempImageDirectory: () -> File = {
            val privateTempDir = File(cacheDir, "images")
            if (!privateTempDir.exists()) privateTempDir.mkdirs()
            privateTempDir
        }

        val directory = tempImageDirectory()
        val photoFile = File(directory, "captured_image.png")
        photoFile.createNewFile()
        return photoFile
    }

    override fun onBackPressed() {
        mBinding.apply {
            imageView.isInvisible = false
            viewFinder.isInvisible = true
        }
        super.onBackPressed()
    }

    override fun finish() {
        cameraExecutor.shutdown()
        super.finish()
    }

    companion object {
        const val REQUEST_CODE_CAMERA_PERMISSION = 0
        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val TAG = "CameraXExample"
    }
}