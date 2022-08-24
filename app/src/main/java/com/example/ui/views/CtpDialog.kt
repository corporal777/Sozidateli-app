package com.example.ui.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.text.util.Linkify
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.toSpannable
import com.example.R
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class CtpDialog(val context: Context) {
    private var onSelect: (isOk: Boolean) -> Unit = {}

    private val layout = LayoutInflater.from(context).inflate(R.layout.dialog_api_error, null)

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(context)

    init {
        val supportEmail = context.getString(R.string.support_email)
        val message = context.getString(R.string.ctp_text).format(supportEmail).toSpannable()
        Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES)

        builder.setView(layout)
        layout.findViewById<TextView>(R.id.tvTitle).text = context.resources.getString(R.string.dear_user_text)
        layout.findViewById<TextView>(R.id.tvMessage).text = message
        layout.findViewById<TextView>(R.id.tvMessage).setOnClickListener {
            layout.findViewById<TextView>(R.id.tvMessage)?.let {
                it.movementMethod = BetterLinkMovementMethod.getInstance()
            }
        }

        layout.findViewById<Button>(R.id.btnPositive).setOnClickListener {
            onSelect.invoke(true)
            alertDialog.dismiss()
        }
        alertDialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 30)
        alertDialog.window?.setBackgroundDrawable(inset)
        alertDialog.show()
    }

    fun setSelectCallback(block: (isOk: Boolean) -> Unit): CtpDialog {
        onSelect = block
        return this
    }
}