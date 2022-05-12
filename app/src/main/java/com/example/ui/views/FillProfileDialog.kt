package com.example.ui.views

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.R

class FillProfileDialog(val context: Context) {

    private var onSelect: (isProfile: Boolean) -> Unit = {}

    private val layout = LayoutInflater.from(context).inflate(R.layout.dialog_fill_profile, null)

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(context)

    init {
        builder.setView(layout)
        val spannableString = SpannableString(context.resources.getText(R.string.fill_in_everything_profile_fields))
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }

            override fun onClick(p0: View) {
                onSelect.invoke(true)
                alertDialog.dismiss()
            }
        }
        spannableString.setSpan(clickableSpan, 62, 69,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(
                ContextCompat.getColor(context, R.color.colorAccent)),
                62, 69, 0)
        layout.findViewById<TextView>(R.id.tvMessage).apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }

        layout.findViewById<Button>(R.id.btnPositive).setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun setSelectCallback(block: (isProfile: Boolean) -> Unit): FillProfileDialog {
        onSelect = block
        return this
    }
}