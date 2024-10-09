package com.example.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.app.R

class ConfirmPhoneDialog(
    val context: Context,
    val text: String,
    val negative: String,
    val positive: String) {

    private var onSelect: (isAgree: Boolean) -> Unit = {}

    private val layout = LayoutInflater.from(context).inflate(R.layout.dialog_confitm_phone, null)

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(context)

    init {
        builder.setView(layout)
        layout.findViewById<TextView>(R.id.tvMessage).text = text
        layout.findViewById<Button>(R.id.btnPositive).apply {
            text = positive
            setOnClickListener {
                onSelect.invoke(true)
                alertDialog.dismiss()
            }
        }
        layout.findViewById<Button>(R.id.btnNegative).apply {
            text = negative
            setOnClickListener {
                onSelect.invoke(false)
                alertDialog.dismiss()
            }
        }
        alertDialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 20)
        alertDialog.window?.setBackgroundDrawable(inset)
        alertDialog.show()
    }

    fun hideDialog() {
        alertDialog.dismiss()
    }

    fun setSelectCallback(block: (isAgree: Boolean) -> Unit): ConfirmPhoneDialog {
        onSelect = block
        return this
    }
}