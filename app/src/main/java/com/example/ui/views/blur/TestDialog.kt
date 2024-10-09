package com.example.ui.views.blur

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LightingColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.renderscript.RenderScript
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.app.databinding.DialogBlurredEventAddedToFavoriteBinding


class BlurredDialog (
    val context: Context,
    val rootView: View,
) {

    private val mBinding = DialogBlurredEventAddedToFavoriteBinding.inflate(LayoutInflater.from(context))

    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBuilder.setCancelable(true)

        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 50)
        mAlertDialog.window?.setBackgroundDrawable(inset)

        mBinding.apply {
            val blurBitmap = captureView(rootView)
            ivBlur.setImageBitmap(BlurHelper.getRoundedBitmap(blurBitmap, 15))
        }
        mAlertDialog.show()
    }

    private fun captureView(view : View): Bitmap {
        val rs = RenderScript.create(context);
        val bitmap : Bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_4444)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        BlurHelper.blurBitmapWithRenderscript(rs, bitmap)
        val paint = Paint()
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        val filter: ColorFilter = LightingColorFilter(0xFF7F7F7F.toInt(), 0x00000000) // darken
        paint.colorFilter = filter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return bitmap
    }
}