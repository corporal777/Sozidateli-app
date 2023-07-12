package com.example.ui.views.dialogs_new

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.renderscript.RenderScript
import com.example.R
import com.example.databinding.DialogEventAddedToFavoriteBinding
import com.example.ui.views.dialogs_new.blur.BlurHelper
import com.yandex.metrica.impl.ob.rs


class EventAddedToFavoriteDialog(
    val action: Int = 0,
    val context: Context
) {

    private val mBinding = DialogEventAddedToFavoriteBinding.inflate(LayoutInflater.from(context))

    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBuilder.setCancelable(true)

        mBinding.apply {
            tvTitle.text =
                if (action == 0) context.getString(R.string.added_to_favorite)
                else context.getString(R.string.removed_from_favorites)
        }
        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 0)
        mAlertDialog.window?.setBackgroundDrawable(inset)
        mAlertDialog.show()


        Handler().postDelayed(Runnable {
            mAlertDialog.dismiss()
        }, 5000)
    }

    private fun createBlurBitmap(){

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
        val filter: ColorFilter = LightingColorFilter(-0x1, 0x00222222) // lighten

        //ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        //ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        paint.colorFilter = filter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return bitmap
    }
}