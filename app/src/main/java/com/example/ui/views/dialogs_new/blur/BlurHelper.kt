package com.example.ui.views.dialogs_new.blur

import android.graphics.*
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

object BlurHelper {
    fun blurBitmapWithRenderscript(rs: RenderScript?, bitmap2: Bitmap?) {
        //this will blur the bitmapOriginal with a radius of 25 and save it in bitmapOriginal
        val input: Allocation = Allocation.createFromBitmap(
            rs,
            bitmap2
        ) //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
        val output: Allocation = Allocation.createTyped(rs, input.getType())
        val script: ScriptIntrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        // must be >0 and <= 25
        script.setRadius(25f)
        script.setInput(input)
        script.forEach(output)
        output.copyTo(bitmap2)
    }

    fun roundCorners(
        bitmap: Bitmap,
        cornerRadiusInPixels: Int,
        captureCircle: Boolean
    ): Bitmap {
        val output: Bitmap = Bitmap.createBitmap(
            bitmap.getWidth(),
            bitmap.getHeight(),
            Bitmap.Config.ARGB_4444
        )
        val canvas = Canvas(output)
        val color = -0x1
        val paint = Paint()
        val rect = Rect(
            0,
            0,
            bitmap.getWidth(),
            bitmap.getHeight()
        )
        val rectF = RectF(rect)
        val roundPx = cornerRadiusInPixels.toFloat()
        paint.setAntiAlias(true)
        canvas.drawARGB(0, 0, 0, 0)
        paint.setColor(color)
        if (captureCircle) {
            canvas.drawCircle(
                rectF.centerX(),
                rectF.centerY(),
                (bitmap.width / 2).toFloat(),
                paint
            )
        } else {
            canvas.drawRoundRect(
                rectF,
                roundPx,
                roundPx,
                paint
            )
        }
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    fun getRoundedCornerAndLightenBitmap(
        bitmap: Bitmap,
        cornerRadiusInPixels: Int,
        captureCircle: Boolean
    ): Bitmap {
        val output: Bitmap = Bitmap.createBitmap(
            bitmap.getWidth(),
            bitmap.getHeight(),
            Bitmap.Config.ARGB_4444
        )
        val canvas = Canvas(output)
        val color = -0x1
        val paint = Paint()
        val rect = Rect(
            0,
            0,
            bitmap.getWidth(),
            bitmap.getHeight()
        )
        val rectF = RectF(rect)
        val roundPx = cornerRadiusInPixels.toFloat()
        paint.setAntiAlias(true)
        canvas.drawARGB(0, 0, 0, 0)
        paint.setColor(color)
        if (captureCircle) {
            canvas.drawCircle(
                rectF.centerX(),
                rectF.centerY(),
                (bitmap.getWidth() / 2).toFloat(),
                paint
            )
        } else {
            canvas.drawRoundRect(
                rectF,
                roundPx,
                roundPx,
                paint
            )
        }
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        val filter: ColorFilter = LightingColorFilter(-0x1, 0x00222222) // lighten
        //ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        paint.setColorFilter(filter)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }
}