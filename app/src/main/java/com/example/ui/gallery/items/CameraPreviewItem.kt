package com.example.ui.gallery.items

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.lifecycle.LifecycleOwner
import com.example.app.R
import com.example.app.databinding.ItemCameraPreviewBinding
import com.example.util.imageCaptureCallback
import com.google.common.util.concurrent.ListenableFuture
import com.xwray.groupie.viewbinding.BindableItem
import java.io.File

class CameraPreviewItem(
    val context: Context,
    val viewLifecycleOwner: LifecycleOwner,
    val onCameraClick: (uri: Uri?, view: ImageView) -> Unit
) : BindableItem<ItemCameraPreviewBinding>(-1001L) {

    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
        ProcessCameraProvider.getInstance(context)
    private lateinit var cameraProvider: ProcessCameraProvider

    private var imageCapture: ImageCapture? = null
    private var isCameraShown = false

    override fun bind(viewBinding: ItemCameraPreviewBinding, position: Int) {
        viewBinding.apply {
            cvImage.setOnClickListener {
                takePhoto(viewBinding)
            }
            if (!isCameraShown) startPreview(viewBinding)
        }
    }

    override fun bind(
        viewBinding: ItemCameraPreviewBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is Boolean) startPreview(viewBinding)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun startPreview(viewBinding: ItemCameraPreviewBinding) {
        isCameraShown = true
        viewBinding.apply {
            previewView.isInvisible = false
            previewImage.isInvisible = true
            try {
                cameraProviderFuture.addListener(Runnable {
                    cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build()
                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//                    val cameraSelector = CameraSelector.Builder()
//                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                        .build()

                    cameraProvider.unbindAll()
                    if (cameraProvider.hasCamera(cameraSelector) && cameraSelector.cameraFilterSet.isNotEmpty()) {
                        cameraProvider.bindToLifecycle(
                            viewLifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    }

                    previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    preview.setSurfaceProvider(previewView.surfaceProvider)

                }, ContextCompat.getMainExecutor(context))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun takePhoto(viewBinding: ItemCameraPreviewBinding) {
        val imageCapture = imageCapture ?: return
        val photoFile = getOutputCacheFilePicture()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(context),
            imageCaptureCallback({
                it.printStackTrace()
                viewBinding.previewImage.transitionName =
                    context.getString(R.string.camera_transition_name)
                onCameraClick.invoke(null, viewBinding.previewImage)
            }, {
                viewBinding.apply {
                    val savedUri = Uri.fromFile(photoFile)
                    previewImage.apply {
                        setImageURI(null)
                        setImageURI(savedUri)
                        transitionName = savedUri.toString()
                        isInvisible = false
                    }
                    previewView.isInvisible = true
                    onCameraClick.invoke(savedUri, previewImage)
                    cameraProvider.unbindAll()
                }
            })
        )

    }

    private fun getOutputCacheFilePicture(): File {
        val tempImageDirectory: () -> File = {
            val privateTempDir = File(context.cacheDir, "images")
            if (!privateTempDir.exists()) privateTempDir.mkdirs()
            privateTempDir
        }

        val directory = tempImageDirectory()
        val photoFile = File(directory, "captured_image.png")
        photoFile.createNewFile()
        return photoFile
    }

    private fun scaleCamera(view: View) {
        view.animate().scaleX(2f).scaleY(2f).setDuration(500).start();
        //val scalingFactor = 2f
        //view.scaleX = scalingFactor
        //view.scaleY = scalingFactor
    }

    override fun initializeViewBinding(view: View) = ItemCameraPreviewBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_camera_preview
}