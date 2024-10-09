package com.example.ui.image

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.app.R
import com.example.app.databinding.ActivityImageViewBinding
import com.example.ui.base.MvpAppCompatActivity
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import dagger.android.AndroidInjection
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.roundToInt


class ImageViewActivity : MvpAppCompatActivity(), ImageViewContract.View {

    @InjectPresenter
    lateinit var presenter: ImageViewPresenter

    @Inject
    lateinit var presenterProvider: Provider<ImageViewPresenter>

    @ProvidePresenter
    fun providePresenter(): ImageViewPresenter = presenterProvider.get()

    private val dummyTarget = object : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            presenter.onBitmapFoundFailed(e ?: java.lang.Exception("onBitmapFailed"))
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            if (bitmap == null) presenter.onBitmapFoundFailed(java.lang.Exception("Bitmap is null"))
            else presenter.onBitmapFound(bitmap)
        }
    }

    private val mBinding = ActivityImageViewBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        postponeEnterTransition()
        intent.extras?.let { ImageViewActivityArgs.fromBundle(it) }?.apply {
            presenter.url = url
            presenter.resource = resource
            presenter.bitmap = bitmap
            presenter.customTransitionName = transitionName
        }

        setContentView(mBinding.root)
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        mBinding.flingLayout.apply {
            positionChangeListener = { _, _, dragRangeRate ->
                mBinding.flingLayout.setBackgroundColor(Color.argb((255 * (1.0F - dragRangeRate)).roundToInt(), 0, 0, 0))
            }
            dismissListener = { onBackPressed() }
        }

        mBinding.photoView.apply {
            setOnMatrixChangeListener { mBinding.flingLayout.isDragEnabled = scale <= 1 }
        }
    }

    override fun setCustomTransitionName(transitionName: String) {
        mBinding.photoView.transitionName = transitionName
    }

    override fun setDefaultTransitionName() {
        mBinding.photoView.transitionName = getString(R.string.image_transition_name)
    }

    override fun setImage(bitmap: Bitmap) {
        mBinding.photoView.setImageBitmap(bitmap)
        startPostponedEnterTransition()
    }

    override fun showError() {
        Toast.makeText(this, R.string.image_load_error, Toast.LENGTH_LONG).show()
        mBinding.photoView.apply {
            setImageResource(R.drawable.ic_broken_image)
            setBackgroundColor(ContextCompat.getColor(context, R.color.image_view_error_background))
        }
        startPostponedEnterTransition()
    }

    override fun findImageBitmap(url: String) {
        if (url.isBlank()) {
            presenter.onBitmapFoundFailed(RuntimeException("url is empty"))
        } else {
            Picasso.get().load(Uri.parse(url)).into(dummyTarget)
        }
    }

    override fun findImageBitmap(resource: Int) = Picasso.get().load(resource).into(dummyTarget)

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when {
            menuItem.itemId == android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(menuItem)
    }
}