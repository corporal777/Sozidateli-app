package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.example.data.models.Optional
import com.example.data.models.asOptional
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.squareup.picasso.Transformation
import io.reactivex.Maybe

class ImageUtil {

    companion object {
        fun loadBitmapFromUrl(
            url: String?,
            transformations: List<Transformation>? = null,
            onResult: (bitmap: Bitmap?) -> Unit
        ) {
            if (url.isNullOrBlank()) {
                onResult(null)
                return
            }

            Picasso.get().load(url)
                .apply { if (transformations != null) transform(transformations) }
                .into(object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit
                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) =
                        onResult(null)

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) =
                        onResult(bitmap)
                })
        }
    }
}

fun String?.loadBitmap(
    transformations: List<Transformation>? = null,
    onResult: (bitmap: Bitmap?) -> Unit
) {
    ImageUtil.loadBitmapFromUrl(this, transformations, onResult)
}

fun String?.loadBitmap(transformations: List<Transformation>? = null): Maybe<Optional<Bitmap>> {
    return Maybe.create { emitter ->
        loadBitmap(transformations) { emitter.onSuccess(it.asOptional()) }
    }
}


fun String?.loadBitmapNew(context: Context): Bitmap? {
    return if (this.isNullOrEmpty()) null
    else Glide.with(context).asBitmap().load(this).submit().get()
}

