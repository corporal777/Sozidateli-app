package com.example.ui.views

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.R
import com.example.databinding.DialogWaitForAcceptBinding

class WaitForAcceptDialog(val activity: Activity, val title: String?, val text: String, val positiveText: String,
val negativeText: String, val isShowCancel: Boolean = false) {

    private var onChangeState: (isAccept: Boolean) -> Unit = {}

    var binding : DialogWaitForAcceptBinding = DataBindingUtil.inflate(
            activity.layoutInflater,
            R.layout.dialog_wait_for_accept,
            null,
            false
    )

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(activity)

    init {
        builder.setView(binding.root)
        binding.tvTitle.isVisible = title != null
        binding.tvTitle.text = title
        binding.tvMessage.text = text
        binding.btnPositive.text = positiveText
        binding.btnNegative.text = negativeText
        binding.btnPositive.setOnClickListener {
            onChangeState(true)
            alertDialog.dismiss()
        }
        binding.btnNegative.setOnClickListener {
            onChangeState(false)
            alertDialog.dismiss()
        }
        binding.btnCancel.isVisible = isShowCancel
        binding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun setSendCodeCallback(block: (isAccept: Boolean) -> Unit): WaitForAcceptDialog {
        onChangeState = block
        return this
    }
}