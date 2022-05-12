package com.example.ui.views

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.R

class ApiErrorDialog(val context: Context, val title: String, val description: String) {

    private var onSelect: (isOk: Boolean) -> Unit = {}

    private val layout = LayoutInflater.from(context).inflate(R.layout.dialog_api_error, null)

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(context)

    init {
        builder.setView(layout)
        layout.findViewById<TextView>(R.id.tvTitle).text = title
        layout.findViewById<TextView>(R.id.tvMessage).text = description
        layout.findViewById<Button>(R.id.btnPositive).setOnClickListener {
            onSelect.invoke(true)
            alertDialog.dismiss()
        }
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun setSelectCallback(block: (isOk: Boolean) -> Unit): ApiErrorDialog {
        onSelect = block
        return this
    }
}