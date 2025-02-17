package com.generator.qrcodegenerator.style

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap

interface BitmapScale {

    fun scale(drawable: Drawable, width: Int, height: Int): Bitmap
    fun scale(logo: QrVectorLogo, width: Float, height: Float): Bitmap

    object FitXY : BitmapScale {
        override fun scale(drawable: Drawable, width: Int, height: Int): Bitmap {
            return drawable.toBitmap(
                width, height,
                config = Bitmap.Config.ARGB_8888
            )
        }

        override fun scale(logo: QrVectorLogo, width: Float, height: Float): Bitmap {
            val bitmap = if (logo.bitmap != null){
                Bitmap.createScaledBitmap(logo.bitmap, width.toInt(), height.toInt(), false)
            } else {
                logo.drawable!!.toBitmap(width.toInt(), height.toInt(), Config.ARGB_8888)
            }
            return bitmap.let { if (it.isMutable) it else it.copy(it.config, true) }
        }

        fun toBitmapScale(drawable: Drawable): Bitmap {
            val bitmap = Bitmap.createBitmap(1054, 1054, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

    }
}

