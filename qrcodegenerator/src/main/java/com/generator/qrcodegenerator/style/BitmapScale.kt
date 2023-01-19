package com.generator.qrcodegenerator.style

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.generator.qrcodegenerator.SerializationProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun interface BitmapScale {

    fun scale(drawable: Drawable, width : Int, height : Int) : Bitmap

    @Serializable
    @SerialName("FitXY")
    object FitXY : BitmapScale {
        override fun scale(drawable: Drawable, width: Int, height: Int): Bitmap {
            return drawable.toBitmap(width,height,
                config = Bitmap.Config.ARGB_8888)
        }
    }

    @Serializable
    @SerialName("CenterCrop")
    object CenterCrop : BitmapScale {
        override fun scale(drawable: Drawable, width: Int, height: Int): Bitmap {
            var iWidth = drawable.intrinsicWidth
            var iHeight = drawable.intrinsicHeight

            if (iWidth == -1 || iHeight == -1 ||
                width / height.toDouble() == iWidth/iHeight.toDouble())
                return drawable.toBitmap(width,height,
                    config = Bitmap.Config.ARGB_8888)

            if (iWidth != width || iHeight != height){
                val scale = maxOf(
                    width.toDouble()/iWidth,
                    height.toDouble()/iHeight
                )

                iWidth = (iWidth * scale).toInt() + 1
                iHeight = (iHeight * scale).toInt() + 1
            }

            val bitmap = drawable.toBitmap(iWidth, iHeight,
                config = Bitmap.Config.ARGB_8888)
            val x = (iWidth - width)/2
            val y = (iHeight - height)/2

            val newBmp = Bitmap.createBitmap(bitmap, x,y, width, height)
            if (newBmp !== bitmap)
                bitmap.recycle()

            return newBmp
        }
    }

    companion object : SerializationProvider {
        override val defaultSerializersModule by lazy(LazyThreadSafetyMode.NONE) {
            SerializersModule {
                polymorphic(BitmapScale::class) {
                    subclass(FitXY::class)
                    subclass(CenterCrop::class)
                }
            }
        }
    }
}

