package com.example.ui.views.blur

import android.content.Context
import android.graphics.*
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.View

object BlurHelper {

    fun loadBitmap(backgroundView: View, targetView: View): Bitmap? {
        val backgroundBounds = Rect()
        backgroundView.getHitRect(backgroundBounds)
        if (!targetView.getLocalVisibleRect(backgroundBounds)) return null

        val blurredBitmap = captureView(backgroundView.context, backgroundView)
        //capture only the area covered by our target view
        val loc = IntArray(2)
        val bgLoc = IntArray(2)
        backgroundView.getLocationInWindow(bgLoc)
        targetView.getLocationInWindow(loc)
        var height = targetView.height
        var y = loc[1]
        if (bgLoc[1] >= loc[1]) {
            //view is going off the screen at the top
            height -= bgLoc[1] - loc[1]
            if (y < 0) y = 0
        }
        if (y + height > blurredBitmap!!.height) {
            height = blurredBitmap.height - y
            Log.d("TAG", "Height = $height")
            if (height <= 0) {
                //below the screen
                return null
            }
        }
        val matrix = Matrix()
        matrix.setScale(0.5f, 0.5f)
        return Bitmap.createBitmap(
            blurredBitmap, targetView.x.toInt(),
            y,
            targetView.measuredWidth,
            height,
            matrix,
            true
        )
    }

    fun createBlurBitmap(context : Context, view : View): Bitmap? {
        val bitmap: Bitmap? = captureView(context, view)
        if (bitmap == null) return null
        else {
            blurBitmapWithRenderscript(RenderScript.create(context), bitmap)
            return bitmap
        }
    }

    fun captureView(context : Context, view: View): Bitmap {
        val rs = RenderScript.create(context);
        val bitmap : Bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_4444)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        //blur it

        //blur it
        BlurHelper.blurBitmapWithRenderscript(rs, bitmap)

        //Make it frosty

        //Make it frosty
        val paint = Paint()
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        //val filter: ColorFilter = LightingColorFilter(-0x1, 0x00222222) // lighten
        val filter: ColorFilter = LightingColorFilter(0xFF7F7F7F.toInt(), 0x00000000) // darken

        //ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        paint.colorFilter = filter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return bitmap
    }

    fun blurBitmapWithRenderscript(rs: RenderScript?, bitmap2: Bitmap?) {
        val input = Allocation.createFromBitmap(rs, bitmap2)
        val output = Allocation.createTyped(
            rs,
            input.type
        )
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
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

    fun getRoundedBitmap(
        bitmap: Bitmap,
        cornerRadiusInPixels: Int,
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
        canvas.drawRoundRect(
            rectF,
            roundPx,
            roundPx,
            paint
        )
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        //ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }
}