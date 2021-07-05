package com.example.ui.views

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.R
import com.example.databinding.DialogChangeStateBinding

class ChangeStateDialog(val activity: Activity, val type: StateType) {

    private var onChangeState: () -> Unit = {}

    var binding : DialogChangeStateBinding = DataBindingUtil.inflate(
            activity.layoutInflater,
            R.layout.dialog_change_state,
            null,
            false
    )

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(activity)

    init {
        builder.setView(binding.root)
        when(type) {
            StateType.SUCCESS -> {
                binding.tvTitle.isVisible = true
                binding.tvMessage.text = activity.resources.getString(R.string.change_status_description)
            }
            else -> {
                binding.tvTitle.isVisible = false
                binding.tvMessage.text = activity.resources.getString(R.string.edit_status_description)
            }
        }
        binding.btnPositive.setOnClickListener {
            onChangeState()
            alertDialog.dismiss()
        }
        binding.btnNegative.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun setSendCodeCallback(block: () -> Unit): ChangeStateDialog {
        onChangeState = block
        return this
    }
}

enum class StateType {
    ERROR, SUCCESS
}