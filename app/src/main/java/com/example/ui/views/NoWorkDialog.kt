package com.example.ui.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.R

class NoWorkDialog(val context: Context) {

    private var onSelect: (isAgree: Boolean) -> Unit = {}

    private val layout = LayoutInflater.from(context).inflate(R.layout.dialog_no_work, null)

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(context)

    init {
        builder.setView(layout)
        layout.findViewById<Button>(R.id.btnPositive).setOnClickListener {
            onSelect.invoke(true)
            alertDialog.dismiss()
        }
        layout.findViewById<Button>(R.id.btnNegative).setOnClickListener {
            onSelect.invoke(false)
            alertDialog.dismiss()
        }

        alertDialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 10)
        alertDialog.window?.setBackgroundDrawable(inset)
        alertDialog.show()
    }

    fun setSelectCallback(block: (isAgree: Boolean) -> Unit): NoWorkDialog {
        onSelect = block
        return this
    }
}