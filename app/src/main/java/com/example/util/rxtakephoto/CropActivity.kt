package com.example.util.rxtakephoto

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.example.R
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.util.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.SingleSubject
import kotlinx.android.synthetic.main.activity_crop.*

class CropActivity : AppCompatActivity() {

    private lateinit var uri: Uri
    private lateinit var cropMode: CropImageView.CropMode
    private lateinit var cropSubject: SingleSubject<Bitmap>
    private var rotation: Int = 0
    private var outputMaxHeight: Int = 0
    private var outputMaxWidth: Int = 0
    private var outputQuality: Int = 100

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

        val extras = intent.extras!!
        uri = extras.getParcelable(ARG_URI)!!
        rotation = extras.getInt(ARG_ROTATION, 0)
        cropMode = (extras.getSerializable(ARG_CROP_MODE) as? CropImageView.CropMode)
                ?: CROP_MODE_DEFAULT
        outputMaxHeight = extras.getInt(ARG_MAX_HEIGHT, 0)
        outputMaxWidth = extras.getInt(ARG_MAX_WIDTH, 0)
        outputQuality = extras.getInt(ARG_OUTPUT_QUALITY, 100)

        val cropSubject = CropCallbackHelper.getRequest(uri.toString())
        if (cropSubject == null) {
            finish()
            return
        }

        this.cropSubject = cropSubject

        cropView.apply {
            setOutputMaxSize(outputMaxWidth, outputMaxHeight)
            setCompressQuality(outputQuality)
            setCompressFormat(Bitmap.CompressFormat.PNG)
            setCropEnabled(true)
            setCropMode(cropMode)
        }

        btnSave.setOnClickListener { crop() }
        btnCancel.setOnClickListener { finish() }

        compositeDisposable += cropView.loadAsCompletable(uri)
                .subscribe({
                    val rotation = rotation - Utils.getExifOrientation(this, uri)
                    if (rotation != 0) {
                        val rotate = when (rotation) {
                            90 -> CropImageView.RotateDegrees.ROTATE_90D
                            180 -> CropImageView.RotateDegrees.ROTATE_180D
                            270 -> CropImageView.RotateDegrees.ROTATE_270D
                            -90 -> CropImageView.RotateDegrees.ROTATE_M90D
                            -180 -> CropImageView.RotateDegrees.ROTATE_M180D
                            -270 -> CropImageView.RotateDegrees.ROTATE_M270D
                            else -> null
                        }
                        if (rotate != null) {
                            cropView.rotateImage(rotate, 0)
                        }
                    }
                }, {
                    cropSubject.onError(it)
                })
    }

    private fun crop() {
        flLoading.isVisible = true
        compositeDisposable.add(
                cropView.cropAsSingle()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            cropSubject.onSuccess(it)
                            flLoading.isVisible = false
                            finish()
                        }, {
                            cropSubject.onError(it)
                            flLoading.isVisible = false
                        })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        val CROP_MODE_DEFAULT = CropImageView.CropMode.CUSTOM

        private const val ARG_URI = "uri"
        private const val ARG_ROTATION = "rotation"
        private const val ARG_CROP_MODE = "crop mode"
        private const val ARG_MAX_WIDTH = "max width"
        private const val ARG_MAX_HEIGHT = "max height"
        private const val ARG_OUTPUT_QUALITY = "output quality"

        fun getStartIntent(
                context: Context,
                uri: Uri,
                orientation: Int,
                outputMaxWidth: Int,
                outputMaxHeight: Int,
                outputQuality: Int,
                cropMode: CropImageView.CropMode
        ): Intent {
            return Intent(context, CropActivity::class.java).apply {
                putExtras(
                        bundleOf(
                                ARG_URI to uri,
                                ARG_ROTATION to orientation,
                                ARG_CROP_MODE to cropMode,
                                ARG_MAX_WIDTH to outputMaxWidth,
                                ARG_MAX_HEIGHT to outputMaxHeight,
                                ARG_OUTPUT_QUALITY to outputQuality
                        )
                )
            }
        }
    }
}