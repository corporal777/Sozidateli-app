package com.example.ui.views.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import com.example.app.R
import com.example.app.databinding.DialogCustomProgressBinding
import com.example.extensions.dp
import com.example.ui.views.CustomProgressBar

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
        val progressBar = CustomProgressBar(context).apply {
            setSize(35.dp)
            setProgressColor(getColor(context, R.color.main_brown_color_new))
        }
        mBinding.progressViewContainer.apply {
            removeAllViews()
            addView(progressBar, 0)
        }
        mAlertDialog.show()
    }

    fun hideDialog() {
        mAlertDialog.dismiss()
    }

}