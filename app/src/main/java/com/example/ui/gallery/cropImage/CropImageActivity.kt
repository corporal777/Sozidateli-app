package com.example.ui.gallery.cropImage

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.view.isInvisible
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.example.R
import com.example.databinding.ActivityImageCropBinding
import com.example.ui.base.BaseCustomActivity
import com.example.ui.gallery.cropImage.cropView.cropWindow.util.CustomCropCallback
import com.example.util.rxtakephoto.CropCallbackHelper
import io.reactivex.subjects.SingleSubject
import kotlinx.android.synthetic.main.activity_crop.*
import javax.inject.Inject
import javax.inject.Provider


class CropImageActivity : BaseCustomActivity<ActivityImageCropBinding>(),
    CropImageContract.View {

    override fun getViewBinding() = ActivityImageCropBinding.inflate(layoutInflater)

    @InjectPresenter
    lateinit var presenter: CropImagePresenter

    @Inject
    lateinit var presenterProvider: Provider<CropImagePresenter>

    @ProvidePresenter
    fun providePresenter(): CropImagePresenter = presenterProvider.get()

    private lateinit var cropSubject: SingleSubject<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.extras?.let { CropImageActivityArgs.fromBundle(it) }?.apply {
            presenter.imageUrl = url
            presenter.customTransitionName = transitionName
        }
        mBinding.btnSave.setOnClickListener {
            presenter.saveCroppedImage(mBinding.customCropView.croppedImageRequest)
        }
        mBinding.btnCancel.setOnClickListener {
            onBackPressed()
        }

        val cropSubject = CropCallbackHelper.getCropFinishedRequest()
        if (cropSubject == null) {
            finish()
            return
        }
        this.cropSubject = cropSubject
    }

    override fun setImage(uri: Uri?) {
        Glide.with(this).load(uri)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(mBinding.imageView).getSize { width, height ->
                startPostponedEnterTransition()
                presenter.onShowImageCrop(uri, width, height)
                isBackEnabled = false
            }
    }

    override fun showImageCrop(uri: Uri?, bitmap: Bitmap?) {
        mBinding.apply {
            imageView.isInvisible = true
            customCropView.apply {
                setAspectRatio(1, 1);
                //setFixedAspectRatio(true);
                setImageBitmap(bitmap)
            }
            isBackEnabled = true
        }
    }

    override fun setCustomTransitionName(transitionName: String) {
        mBinding.imageView.transitionName = transitionName
    }

    override fun setDefaultTransitionName() {
        mBinding.imageView.transitionName = getString(R.string.image_transition_name)
    }

    override fun showProgressDialog() = mProgressDialog.showDialog()
    override fun hideProgressDialog() = mProgressDialog.hideDialog()
    override fun closeCropActivity() = onBackPressed()

    override fun onBackPressed() {
        if (isBackEnabled) {
            mBinding.imageView.isInvisible = false
            mBinding.customCropView.isInvisible = true
            super.onBackPressed()
        } else return
    }

    override fun finish() {
        super.finish()
        cropSubject.onSuccess(presenter.isCropFinished)
    }

}