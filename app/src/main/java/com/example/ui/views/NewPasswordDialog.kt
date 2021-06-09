package com.example.ui.views

import android.app.Activity
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.example.R
import com.example.ui.views.passwordView.PasswordCustomView

class NewPasswordDialog(val activity: Activity) {

    var password = ""

    private var onSelect: (password: String) -> Unit = {}

    private val layout = LayoutInflater.from(activity).inflate(R.layout.dialog_password_recovery, null)

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(activity)

    init {
        builder.setView(layout)
        val passwordF = layout.findViewById<PasswordCustomView>(R.id.password)
        val save = layout.findViewById<AppCompatButton>(R.id.btnSave)
        passwordF.setPasswordValidCallback {
            this@NewPasswordDialog.password = it.password?: ""
            save.isEnabled = it.isValid
        }
        save.isEnabled = false
        save.setOnClickListener {
            onSelect(password)
            alertDialog.dismiss()
        }
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun setSelectCallback(block: (password: String) -> Unit): NewPasswordDialog {
        onSelect = block
        return this
    }
}