package com.example.ui.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.R


class GetMaxStateDialog(val context: Context) {

    private var onSelect: (isProfile: Boolean) -> Unit = {}
    private var onCancel: (isCancel: Boolean) -> Unit = {}

    private val layout = LayoutInflater.from(context).inflate(R.layout.dialog_get_max_state, null)

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(context)

    init {
        builder.setView(layout)
//        layout.findViewById<TextView>(R.id.tvTitle).text =
//            context.resources.getString(R.string.get_max_state_title)

        layout.findViewById<TextView>(R.id.tvMessage).apply {
            text = context.resources.getString(R.string.get_max_state_text)
            movementMethod = LinkMovementMethod.getInstance()
        }

        layout.findViewById<Button>(R.id.btnGetMax).setOnClickListener {
            onSelect.invoke(true)
            alertDialog.dismiss()
        }

        layout.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 10)
        alertDialog.window?.setBackgroundDrawable(inset)
        alertDialog.show()
    }

    fun setSelectCallback(block: (isProfile: Boolean) -> Unit): GetMaxStateDialog {
        onSelect = block
        return this
    }

    fun setCancelCallback(block: (isCancel: Boolean) -> Unit): GetMaxStateDialog {
        onCancel = block
        return this
    }
}