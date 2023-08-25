package com.example.ui.views.galleryView

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RelativeLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.R
import com.example.databinding.ItemCameraPreviewBinding
import com.example.ui.main.MainActivity
import com.google.common.util.concurrent.ListenableFuture
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.GroupieViewHolder

class CameraPreviewItem(
    val context: Context,
    val lifecycleOwner: LifecycleOwner,
    val onCameraClick: () -> Unit
) : BindableItem<ItemCameraPreviewBinding>() {

    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
        ProcessCameraProvider.getInstance(context)

    private var cameraProvider: ProcessCameraProvider? = null


    override fun bind(viewBinding: ItemCameraPreviewBinding, position: Int) {
        viewBinding.apply {
            cvImage.setOnClickListener {
                onCameraClick.invoke()
            }
            startPreview(previewView)
        }

    }

    private fun startPreview(previewView: PreviewView) {
        try {

            cameraProviderFuture.addListener(Runnable {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build()

                val cameraSelector: CameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                cameraProvider?.let {
                    it.unbindAll()
                    it.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                }

                val surfaceProvider = previewView.surfaceProvider
                previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                preview.setSurfaceProvider(surfaceProvider)

            }, ContextCompat.getMainExecutor(context))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun getLayout(): Int = R.layout.item_camera_preview
}