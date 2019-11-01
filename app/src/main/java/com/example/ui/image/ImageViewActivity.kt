package com.example.ui.image

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.TransitionSet
import android.transition.TransitionSet.ORDERING_TOGETHER
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.MvpAppCompatActivity
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_image_view.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            val transition = TransitionSet().apply {
                ordering = ORDERING_TOGETHER
                addTransition(ChangeBounds())
                addTransition(ChangeTransform())
                addTransition(ChangeImageTransform())
            }

            sharedElementEnterTransition = transition
            sharedElementReturnTransition = transition
            sharedElementExitTransition = transition
        }

        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        postponeEnterTransition()
        intent.extras?.let { ImageViewActivityArgs.fromBundle(it) }?.apply {
            presenter.url = url
            presenter.resource = resource
            presenter.bitmap = bitmap
            presenter.customTransitionName = transitionName
        }

        setContentView(R.layout.activity_image_view)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        flingLayout.apply {
            positionChangeListener = { _, _, dragRangeRate ->
                flingLayout.setBackgroundColor(Color.argb((255 * (1.0F - dragRangeRate)).roundToInt(), 0, 0, 0))
            }
            dismissListener = { onBackPressed() }
        }

        photoView.apply {
            setOnMatrixChangeListener { flingLayout.isDragEnabled = scale <= 1 }
        }
    }

    override fun setCustomTransitionName(transitionName: String) {
        photoView.transitionName = transitionName
    }

    override fun setDefaultTransitionName() {
        photoView.transitionName = getString(R.string.image_transition_name)
    }

    override fun setImage(bitmap: Bitmap) {
        photoView.setImageBitmap(bitmap)
        startPostponedEnterTransition()
    }

    override fun showError() {
        Toast.makeText(this, R.string.image_load_error, Toast.LENGTH_LONG).show()
        photoView.apply {
            setImageResource(R.drawable.ic_broken_image)
            setBackgroundColor(ContextCompat.getColor(context, R.color.image_view_error_background))
        }
        startPostponedEnterTransition()
    }

    override fun findImageBitmap(url: String) = Picasso.get().load(url).into(dummyTarget)
    override fun findImageBitmap(resource: Int) = Picasso.get().load(resource).into(dummyTarget)


    override fun onBackPressed() {
        finish()
    }

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