package com.example.util

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.FloatRange
import androidx.annotation.RawRes
import androidx.core.content.res.ResourcesCompat
import com.example.R

object AuthBackground {

    private var bitmap: Bitmap? = null

    fun get(res: Resources): Drawable {
        val bitmap = this.bitmap
        return asDrawable(res, if (bitmap != null && !bitmap.isRecycled) bitmap else create(res))
    }

    fun clear() {
        bitmap?.recycle()
        bitmap = null
    }

    private fun create(
            res: Resources,
            @RawRes resId: Int = R.raw.background_auth
    ): Bitmap {
        val (imageHeight: Int, imageWidth: Int) = BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeResource(res, resId, this)
            outHeight to outWidth
        }

        val (displayHeight: Int, displayWidth: Int) = res.displayMetrics.run { heightPixels to widthPixels }

        val imageRatio = imageWidth.toFloat() / imageHeight.toFloat()
        val displayRatio = displayWidth.toFloat() / displayHeight.toFloat()

        val (heightScale: Float, widthScale: Float) = if (displayRatio > imageRatio) {
            displayRatio to 1f
        } else {
            1f to displayRatio
        }

        val (top: Int, bottom: Int) = cropSide(imageHeight, (displayHeight * heightScale).toInt())
        val (left: Int, right: Int) = cropSide(imageWidth, (displayWidth * widthScale).toInt(), 0.6f)

        return BitmapRegionDecoder.newInstance(res.openRawResource(resId), false).decodeRegion(
                Rect(left, top, right, bottom),
                BitmapFactory.Options()
        ).apply {
            bitmap = this
        }
    }

    private fun cropSide(imageSize: Int, displaySize: Int, @FloatRange(from = -1.0, to = 1.0) move: Float = 0f): Pair<Int, Int> {
        return if (imageSize > displaySize) {
            val difference = imageSize - displaySize
            val start = (difference / 2 + ((difference / 2) * move)).toInt()
            start to start + displaySize
        } else 0 to imageSize
    }

    private fun asDrawable(res: Resources, bitmap: Bitmap): Drawable {
        return BitmapDrawable(res, bitmap).apply {
            setColorFilter(ResourcesCompat.getColor(res, R.color.auth_background_overlay, null), PorterDuff.Mode.DARKEN)
        }
    }
}