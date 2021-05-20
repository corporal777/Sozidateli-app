package com.example.ui.views

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import com.example.R
import com.example.databinding.DialogAddPhoneEmailBinding
import com.example.util.AuthValidateUtil
import com.example.util.Utils

class AddPhoneEmailDialog(val activity: Activity, val type: RegisterDataType) {

    private var onSelect: (result: PhoneEmailResult) -> Unit = {}
    private var onSendCode: () -> Unit = {}

    var binding : DialogAddPhoneEmailBinding = DataBindingUtil.inflate(
            activity.layoutInflater,
            R.layout.dialog_add_phone_email,
            null,
            false
    )

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(activity)

    init {
        builder.setView(binding.root)
        binding.btnPositive.setOnClickListener {
            isProgressVisible(true)
            onSelect.invoke(PhoneEmailResult(type, binding.etLogin.text.toString()))
        }
        binding.tvCode.setOnClickListener {
            onSendCode()
        }
        binding.btnPositive.isEnabled = false
        binding.etLogin.doAfterTextChanged {
            when (type) {
                RegisterDataType.EMAIL -> {
                    binding.btnPositive.isEnabled = AuthValidateUtil.isValidEmail(it.toString())
                }
                RegisterDataType.PHONE, RegisterDataType.CHANGE_PHONE -> {
                    binding.btnPositive.isEnabled = Utils.newPhoneValidator(activity, it.toString())
                }
                RegisterDataType.CODE -> {
                    binding.btnPositive.isEnabled = it.toString().length == CODE_SIZE
                }
            }
        }
        binding.btnNegative.setOnClickListener {
            alertDialog.dismiss()
        }
        setData(null)
        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun setData(phone: String?) {
        when (type) {
            RegisterDataType.EMAIL -> {
                binding.tvTitle.text = activity.resources.getString(R.string.add_email_dialog_title)
                binding.tvMessage.text = activity.resources.getString(R.string.add_email_dialog_text)
                binding.etLogin.setHint(R.string.email)
                binding.tvCode.isVisible = false
            }
            RegisterDataType.PHONE -> {
                binding.tvTitle.text = activity.resources.getString(R.string.add_phone_dialog_title)
                binding.tvMessage.text = activity.resources.getString(R.string.add_phone_dialog_text)
                binding.etLogin.setHint(R.string.search_filter_phone)
                binding.tvCode.isVisible = false
            }
            RegisterDataType.CODE -> {
                binding.tvTitle.text = activity.resources.getString(R.string.code_dialog_title)
                binding.tvMessage.text = activity.resources.getString(R.string.code_dialog_text, phone)
                binding.etLogin.setHint(R.string.enter_code_btn_text)
                binding.tvCode.isVisible = true
            }
            RegisterDataType.CHANGE_PHONE -> {
                binding.tvTitle.text = activity.resources.getString(R.string.new_phone_number)
                binding.tvMessage.text = activity.resources.getString(R.string.add_phone_dialog_text)
                binding.etLogin.setHint(R.string.search_filter_phone)
                binding.tvCode.isVisible = false
            }
        }
    }

    fun isProgressVisible(vis: Boolean) {
        binding.progressBar2.isVisible = vis
    }

    fun setPhoneForCode(phone: String) {
        setData(phone)
    }

    fun setSelectCallback(block: (result: PhoneEmailResult) -> Unit): AddPhoneEmailDialog {
        onSelect = block
        return this
    }

    fun hideDialog() {
        alertDialog.dismiss()
    }

    fun setSendCodeCallback(block: () -> Unit): AddPhoneEmailDialog {
        onSendCode = block
        return this
    }

    companion object {
        const val CODE_SIZE = 6
    }
}

data class PhoneEmailResult(
        val type: RegisterDataType,
        val value: String
)

enum class RegisterDataType {
    EMAIL, PHONE, CODE, CHANGE_PHONE
}