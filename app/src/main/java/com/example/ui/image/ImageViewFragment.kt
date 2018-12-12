package com.example.ui.image

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import bundleOf
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_image_view.*
import javax.inject.Inject
import javax.inject.Provider

class ImageViewFragment : MvpAppCompatFragment(), ImageViewContract.View {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_view, container, false)
    }

    override fun setImage(bitmap: Bitmap) {
        photoView.setImageBitmap(bitmap)
    }

    override fun showError() {
        Toast.makeText(context, R.string.image_load_error, Toast.LENGTH_LONG).show()
        photoView.apply {
            setImageResource(R.drawable.ic_broken_image)
            setBackgroundColor(ContextCompat.getColor(context, R.color.image_view_error_background))
        }
    }

    override fun findImageBitmap(url: String) = Picasso.get().load(url).into(dummyTarget)
    override fun findImageBitmap(resource: Int) = Picasso.get().load(resource).into(dummyTarget)

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