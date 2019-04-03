package com.example.util

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.squareup.picasso.Transformation

class ImageUtil {

    companion object {
        fun loadBitmapFromUrl(url: String?, transformations: List<Transformation>? = null, onResult: (bitmap: Bitmap?) -> Unit) {
            if (url.isNullOrBlank()) {
                onResult(null)
                return
            }

            Picasso.get().load(url)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .apply { if (transformations != null) transform(transformations) }
                    .into(object : Target {
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit
                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) = onResult(null)
                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) = onResult(bitmap)
                    })
        }
    }
}

fun String?.loadBitmap(transformations: List<Transformation>? = null, onResult: (bitmap: Bitmap?) -> Unit) {
    ImageUtil.loadBitmapFromUrl(this, transformations, onResult)
}