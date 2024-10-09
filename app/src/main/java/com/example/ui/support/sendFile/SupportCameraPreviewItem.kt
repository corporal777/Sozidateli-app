package com.example.ui.support.sendFile

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
import com.example.app.databinding.ItemSupportCameraPreviewBinding
import com.example.util.imageCaptureCallback
import com.google.common.util.concurrent.ListenableFuture
import com.xwray.groupie.databinding.BindableItem
import java.io.File

class SupportCameraPreviewItem (
    val context: Context,
    val viewLifecycleOwner: LifecycleOwner,
    val onCameraClick: () -> Unit
) : BindableItem<ItemSupportCameraPreviewBinding>(-1001L) {

    private var cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    private lateinit var cameraProvider : ProcessCameraProvider
    private var isCameraShown = false

    override fun bind(viewBinding: ItemSupportCameraPreviewBinding, position: Int) {
        viewBinding.apply {
            cvImage.setOnClickListener {
                onCameraClick.invoke()
            }
            if (!isCameraShown) startPreview(viewBinding)
        }
    }

    override fun bind(
        viewBinding: ItemSupportCameraPreviewBinding,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is Boolean) startPreview(viewBinding)
        }
    }

    private fun startPreview(viewBinding: ItemSupportCameraPreviewBinding) {
        isCameraShown = true
        viewBinding.apply {
            try {
                cameraProviderFuture.addListener(Runnable {
                    cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build()
                    val cameraSelector: CameraSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview)
                    previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    preview.setSurfaceProvider(previewView.surfaceProvider)

                }, ContextCompat.getMainExecutor(context))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_support_camera_preview
}