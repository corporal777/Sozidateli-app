package com.example.ui.views

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.example.R
import com.example.databinding.DialogAddPhoneEmailBinding
import com.example.util.AuthValidateUtil
import com.example.util.Utils

class AddPhoneEmailDialog(val context: Context, val type: ContactsType) {

    private var onSelectPhone: (result: String) -> Unit = {}
    private var onSelectEmail: (result: String) -> Unit = {}
    private var onNegativeClick: () -> Unit = {}

    private val binding = DialogAddPhoneEmailBinding.inflate(LayoutInflater.from(context))

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(context)

    init {
        builder.setView(binding.root)

        binding.btnPositive.apply {
            isEnabled = false
            setOnClickListener {
                if (type == ContactsType.EMAIL) onSelectEmail.invoke(binding.etLogin.text.toString())
                else onSelectPhone.invoke(Utils.validatePhoneBeforeSend(binding.etLogin.text.toString()))
            }
        }
        binding.btnNegative.setOnClickListener {
            onNegativeClick.invoke()
            alertDialog.dismiss()
        }
        binding.etLogin.doAfterTextChanged {
            when (type) {
                ContactsType.EMAIL -> {
                    binding.btnPositive.isEnabled = AuthValidateUtil.isValidEmail(it.toString())
                }

                ContactsType.PHONE -> {
                    binding.btnPositive.isEnabled = Utils.isPhoneNumberValid(it.toString())
                }
            }
        }

        setData()
        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun setData() {
        when (type) {
            ContactsType.EMAIL -> {
                binding.tvTitle.text = context.getString(R.string.add_email_dialog_title)
                binding.tvMessage.text = context.getString(R.string.add_email_dialog_text)
                binding.etLogin.setHint(R.string.email)
            }

            ContactsType.PHONE -> {
                binding.tvTitle.text = context.getString(R.string.add_phone_dialog_title)
                binding.tvMessage.text = context.getString(R.string.add_phone_dialog_text)
                binding.etLogin.setHint(R.string.search_filter_phone)
            }
        }
    }

    fun setSelectPhoneCallback(block: (result: String) -> Unit): AddPhoneEmailDialog {
        onSelectPhone = block
        return this
    }

    fun setSelectEmailCallback(block: (result: String) -> Unit): AddPhoneEmailDialog {
        onSelectEmail = block
        return this
    }

    fun setNegativeClickCallback(block: () -> Unit): AddPhoneEmailDialog {
        onNegativeClick = block
        return this
    }

    fun hideDialog() {
        alertDialog.dismiss()
    }
}

enum class ContactsType {
    EMAIL, PHONE
}