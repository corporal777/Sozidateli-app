package com.example.ui.views

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import com.example.R
import com.example.databinding.DialogSetPasswordBinding

class SetPasswordDialog(val activity: Activity) {

    private var onSelect: (password: String) -> Unit = {}

    var binding : DialogSetPasswordBinding = DataBindingUtil.inflate(
            activity.layoutInflater,
            R.layout.dialog_set_password,
            null,
            false
    )

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(activity)

    init {
        builder.setView(binding.root)
        binding.btnNegative.setOnClickListener {
            alertDialog.dismiss()
        }
        binding.etPassword.doAfterTextChanged {
            binding.tilPassword.error = null
        }
        binding.btnPositive.setOnClickListener {
            if (!binding.etPassword.text.isNullOrEmpty())
                onSelect(binding.etPassword.text.toString())
            else
                binding.tilPassword.error = activity.resources.getString(R.string.auth_error_no_password)
        }
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun hideDialog() {
        alertDialog.dismiss()
    }

    fun isProgressVisible(vis: Boolean) {
        binding.progressBar2.isVisible = vis
    }

    fun setSelectCallback(block: (password: String) -> Unit): SetPasswordDialog {
        onSelect = block
        return this
    }
}