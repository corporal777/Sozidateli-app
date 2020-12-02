package com.example.ui.views

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.R

class InfoDialog(val context: Context, val title: String) {

    private var onSelect: (isOk: Boolean) -> Unit = {}

    private val layout = LayoutInflater.from(context).inflate(R.layout.dialog_info, null)

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(context)

    init {
        builder.setView(layout)
        layout.findViewById<TextView>(R.id.tvMessage).text = title
        layout.findViewById<Button>(R.id.btnPositive).setOnClickListener {
            onSelect.invoke(true)
            alertDialog.dismiss()
        }
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun setSelectCallback(block: (isOk: Boolean) -> Unit): InfoDialog {
        onSelect = block
        return this
    }
}