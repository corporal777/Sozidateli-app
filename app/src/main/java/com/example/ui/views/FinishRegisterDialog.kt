package com.example.ui.views

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.R

class FinishRegisterDialog(val context: Context) {

    private var onSelect: (isOk: Boolean) -> Unit = {}

    private val layout = LayoutInflater.from(context).inflate(R.layout.dialog_finish_register, null)

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(context)

    init {
        builder.setView(layout)
        val supportEmail = context.resources.getString(R.string.support_email)
        val description = context.resources.getString(R.string.auth_register_confirm_email_message).format(supportEmail)
        layout.findViewById<TextView>(R.id.tvMessage).text = description
        layout.findViewById<Button>(R.id.btnPositive).setOnClickListener {
            onSelect.invoke(true)
            alertDialog.dismiss()
        }
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun setSelectCallback(block: (isOk: Boolean) -> Unit): FinishRegisterDialog {
        onSelect = block
        return this
    }
}