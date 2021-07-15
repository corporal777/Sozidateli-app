package com.example.ui.views

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.R
import com.example.databinding.DialogWarningBinding

class WarningDialog(val activity: Activity, val text: String) {

    private var onSelect: (isAccept: Boolean) -> Unit = {}

    var binding : DialogWarningBinding = DataBindingUtil.inflate(
            activity.layoutInflater,
            R.layout.dialog_warning,
            null,
            false
    )

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(activity)

    init {
        builder.setView(binding.root)
        binding.tvPasswordDescription.text = text
        binding.btnPositive.setOnClickListener {
            onSelect(true)
            alertDialog.dismiss()
        }
        binding.btnNegative.setOnClickListener {
            onSelect(false)
            alertDialog.dismiss()
        }
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun setSelectCallback(block: (isAccept: Boolean) -> Unit): WarningDialog {
        onSelect = block
        return this
    }
}