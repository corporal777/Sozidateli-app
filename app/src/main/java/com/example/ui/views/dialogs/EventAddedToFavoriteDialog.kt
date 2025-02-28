package com.example.ui.views.dialogs

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Handler
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.app.R
import com.example.app.databinding.DialogEventAddedToFavoriteBinding


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



        Handler().postDelayed(Runnable {
            mAlertDialog.dismiss()
        }, 2000)
    }

    fun show() = mAlertDialog.show()
}