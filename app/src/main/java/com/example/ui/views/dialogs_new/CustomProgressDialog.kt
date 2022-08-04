package com.example.ui.views.dialogs_new

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Handler
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.DialogCustomProgressBinding
import com.example.databinding.DialogEventAddedToFavoriteBinding
import com.example.extensions.dp
import com.example.ui.views.CustomProgressView

class CustomProgressDialog(val context: Context) {

    private val mBinding = DialogCustomProgressBinding.inflate(LayoutInflater.from(context))

    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBuilder.setCancelable(false)

        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 0)
        mAlertDialog.window?.setBackgroundDrawable(inset)
        //mAlertDialog.window?.setDimAmount(0.5f)
    }

    fun showDialog() {
        mBinding.progressViewContainer.removeAllViews()
        val progressBar = CustomProgressView(context)
        progressBar.setSize(35.dp)
        progressBar.setProgressColor(
            ContextCompat.getColor(
                context,
                R.color.main_brown_color_new
            )
        )
        mBinding.progressViewContainer.apply {
            addView(progressBar, 0)
        }
        mAlertDialog.show()
    }

    fun hideDialog() {
        mAlertDialog.dismiss()
    }

}