package com.example.ui.views.dialogs.blur

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Handler
import android.renderscript.RenderScript
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.R
import com.example.databinding.DialogBlurredEventAddedToFavoriteBinding
import com.example.ui.views.blur.BlurHelper

class EventAddedToFavoriteBlurredDialog (
    val action: Int = 0,
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
        val inset = InsetDrawable(back, 0)
        mAlertDialog.window?.setBackgroundDrawable(inset)
        mAlertDialog.window?.setDimAmount(0f)

        mBinding.apply {
            tvTitle.text =
                if (action == 0) context.getString(R.string.added_to_favorite)
                else context.getString(R.string.removed_from_favorites)

            val blurBitmap = captureView(rootView)
            //val blurBitmap = loadBitmap(rootView, targetView)


            ivBlur.setImageBitmap(blurBitmap)
        }



        mAlertDialog.show()
        Handler().postDelayed(Runnable {
            mAlertDialog.dismiss()
        }, 5000)
    }

    private fun loadBitmap(backgroundView: View, targetView: View): Bitmap? {
        val backgroundBounds = Rect()
        backgroundView.getHitRect(backgroundBounds)

        if (!targetView.getGlobalVisibleRect(backgroundBounds)) {
            // NONE of the imageView is within the visible window
            return null
        }
        val blurredBitmap = captureView(backgroundView)
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
        if (y + height > blurredBitmap.height) {
            height = blurredBitmap.height - y
            Log.d("TAG", "Height = $height")
            if (height <= 0) {
                //below the screen
                return null
            }
        }
        val matrix = Matrix()
        //half the size of the cropped bitmap
        //to increase performance, it will also
        //increase the blur effect.
        matrix.setScale(0.5f, 0.5f)


        return Bitmap.createBitmap(
            blurredBitmap,
            targetView.x.toInt(),
            y,
            targetView.measuredWidth,
            height,
            matrix,
            true
        )
    }


    private fun captureView(view : View): Bitmap {
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
}