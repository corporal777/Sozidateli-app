package com.example.ui.views.dialogs_new

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Handler
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.databinding.DialogEventAddedToFavoriteBinding


class EventAddedToFavoriteDialog(val context: Context) {

    private val mBinding = DialogEventAddedToFavoriteBinding.inflate(LayoutInflater.from(context))

    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBuilder.setCancelable(true)


        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 0)
        mAlertDialog.window?.setBackgroundDrawable(inset)
        mAlertDialog.show()


        Handler().postDelayed(Runnable {
            mAlertDialog.dismiss()
        }, 2000)
    }

}