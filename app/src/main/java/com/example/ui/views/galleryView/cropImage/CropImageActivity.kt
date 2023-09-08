package com.example.ui.views.galleryView.cropImage

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.FragmentImageCropBinding
import com.example.ui.base.MvpAppCompatActivity
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.callback.LoadCallback
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_crop.*

import javax.inject.Inject
import javax.inject.Provider

class CropImageActivity : MvpAppCompatActivity(), CropImageContract.View {

    @InjectPresenter
    lateinit var presenter: CropImagePresenter

    @Inject
    lateinit var presenterProvider: Provider<CropImagePresenter>

    @ProvidePresenter
    fun providePresenter(): CropImagePresenter = presenterProvider.get()

    lateinit var mBinding: FragmentImageCropBinding
    private var isBackEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        mBinding = FragmentImageCropBinding.inflate(layoutInflater)

        overridePendingTransition(0, 0)
        postponeEnterTransition()

        setContentView(mBinding.root)

        intent.extras?.let { CropImageActivityArgs.fromBundle(it) }?.apply {
            presenter.imageUrl = url
            presenter.customTransitionName = transitionName
        }

        mBinding.cropView.apply {
            setOutputMaxSize(1024, 1024)
            setCompressQuality(100)
            setCompressFormat(Bitmap.CompressFormat.PNG)
            setCropEnabled(true)
            setCropMode(CropImageView.CropMode.SQUARE)
            setAnimationEnabled(false)
        }
        mBinding.btnSave.setOnClickListener {
            presenter.saveCroppedImage(mBinding.cropView)
        }
        mBinding.btnCancel.setOnClickListener {
            finish()
        }
    }

    override fun setImage(uri: Uri?) {
        uri?.let { mBinding.imageView.setImageURI(it) }
        startPostponedEnterTransition()
        presenter.onShowImageCrop(uri)
        isBackEnabled = false
    }

    override fun showImageCrop(uri: Uri?) {
        mBinding.cropView.loadAsync(uri, object : LoadCallback {
            override fun onError(e: Throwable?) {
                e?.printStackTrace()
                isBackEnabled = true
            }

            override fun onSuccess() {
                mBinding.apply {
                    imageView.isInvisible = true
                    cropView.isInvisible = false
                    isBackEnabled = true
                }
            }

        })
    }

    override fun setCustomTransitionName(transitionName: String) {
        mBinding.imageView.transitionName = transitionName
    }

    override fun setDefaultTransitionName() {
        mBinding.imageView.transitionName = getString(R.string.image_transition_name)
    }

    override fun onBackPressed() {
        if (isBackEnabled) super.onBackPressed()
        else return
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

}