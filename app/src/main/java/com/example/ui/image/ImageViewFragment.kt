package com.example.ui.image

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.transition.*
import android.support.transition.TransitionSet.ORDERING_TOGETHER
import android.support.v4.content.ContextCompat
import android.widget.Toast
import bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fragment_image_view.*
import javax.inject.Inject
import javax.inject.Provider

class ImageViewFragment : BaseFragment(), ImageViewContract.View {

    @InjectPresenter
    lateinit var presenter: ImageViewPresenter

    @Inject
    lateinit var presenterProvider: Provider<ImageViewPresenter>

    @ProvidePresenter
    fun providePresenter(): ImageViewPresenter = presenterProvider.get().apply {
        url = arguments?.getString(ARG_IMAGE_URL)
        resource = arguments?.getInt(ARG_IMAGE_RESOURCE)
        bitmap = arguments?.getParcelable(ARG_IMAGE_BITMAP)
    }

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

    init {
        val transition = TransitionSet().apply {
            ordering = ORDERING_TOGETHER
            addTransition(ChangeBounds())
            addTransition(ChangeTransform())
            addTransition(ChangeImageTransform())
        }

        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
        enterTransition = Fade()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
    }

    override fun setImage(bitmap: Bitmap) {
        photoView.setImageBitmap(bitmap)
        startPostponedEnterTransition()
    }

    override fun showError() {
        Toast.makeText(context, R.string.image_load_error, Toast.LENGTH_LONG).show()
        photoView.apply {
            setImageResource(R.drawable.ic_broken_image)
            setBackgroundColor(ContextCompat.getColor(context, R.color.image_view_error_background))
        }
        startPostponedEnterTransition()
    }

    override fun findImageBitmap(url: String) = Picasso.get().load(url).into(dummyTarget)
    override fun findImageBitmap(resource: Int) = Picasso.get().load(resource).into(dummyTarget)

    override fun isShowToolbar() = true
    override fun layout() = R.layout.fragment_image_view

    companion object {
        const val ARG_IMAGE_URL = "image_url"
        const val ARG_IMAGE_RESOURCE = "image_resource"
        const val ARG_IMAGE_BITMAP = "image_bitmap"

        fun newInstance(url: String) = ImageViewFragment().apply {
            arguments = bundleOf(ARG_IMAGE_URL to url)
        }

        fun newInstance(@DrawableRes resource: Int) = ImageViewFragment().apply {
            arguments = bundleOf(ARG_IMAGE_RESOURCE to resource)
        }

        fun newInstance(bitmap: Bitmap) = ImageViewFragment().apply {
            arguments = bundleOf(ARG_IMAGE_BITMAP to bitmap)
        }
    }
}