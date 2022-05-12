package com.example.ui.views

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.text.method.ScrollingMovementMethod
import android.view.Display
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.R

class InfoDialog(val context: Context, val title: String, val activity: Activity) {

    private var onSelect: (isOk: Boolean) -> Unit = {}

    private val layout = LayoutInflater.from(context).inflate(R.layout.dialog_info, null)

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(context)

    init {
        builder.setView(layout)
        val display: Display = activity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        layout.findViewById<TextView>(R.id.tvMessage).apply {
            maxWidth = size.x - 150
            text = title
            movementMethod = ScrollingMovementMethod()
        }
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