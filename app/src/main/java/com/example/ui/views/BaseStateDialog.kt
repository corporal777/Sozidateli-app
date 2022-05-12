package com.example.ui.views

import android.app.Activity
import android.graphics.Point
import android.view.Display
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.R

class BaseStateDialog(val title: String, val activity: Activity) {

    private var onSelect: (isOk: Boolean) -> Unit = {}

    private val layout = LayoutInflater.from(activity).inflate(R.layout.dialog_base_state, null)

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(activity).setCancelable(false)

    init {
        builder.setView(layout)
        val display: Display = activity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        layout.findViewById<TextView>(R.id.tvMessage).apply {
            text = title
        }
        layout.findViewById<Button>(R.id.btnPositive).setOnClickListener {
            onSelect.invoke(true)
            alertDialog.dismiss()
        }
        alertDialog = builder.create()
        alertDialog.show()

    }

    fun setSelectCallback(block: (isOk: Boolean) -> Unit): BaseStateDialog {
        onSelect = block
        return this
    }
}