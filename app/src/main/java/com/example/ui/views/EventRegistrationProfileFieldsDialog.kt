package com.example.ui.views

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.StyleSpan
import androidx.appcompat.app.AlertDialog
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.example.R
import com.example.util.ClickableSpan
import kotlinx.android.synthetic.main.dialog_event_registration_profile_fields.view.*
import kotlinx.android.synthetic.main.item_action_button.view.*

class EventRegistrationProfileFieldsDialog(
        context: Context,
        private val fields: List<String>,
        private val onShowProfileClick: () -> Unit
) : AlertDialog(context) {

    init {
        val actionButtonView = layoutInflater.inflate(R.layout.dialog_event_registration_profile_fields, null).apply {
            tvMessage.movementMethod = LinkMovementMethod.getInstance()
            tvMessage.text = buildMessage()
            btnOk.btnAction.text = context.getString(R.string.ok)
            btnOk.btnAction.setOnClickListener { dismiss() }
        }

        setView(actionButtonView)
    }

    private fun buildMessage(): CharSequence {
        return context.getText(R.string.event_register_check_fields).let {
            val clickableText = context.getString(R.string.event_register_check_click_field)
            val clickableIndex = it.indexOf(clickableText)
            val description = if (clickableIndex >= 0) {
                val endIndex = clickableIndex + clickableText.length
                it.toSpannable().apply {
                    set(clickableIndex, endIndex, StyleSpan(Typeface.BOLD))
                    set(clickableIndex, endIndex, ClickableSpan(drawUnderline = false) {
                        dismiss()
                        onShowProfileClick()
                    })
                }
            } else {
                it
            }

            val stringBuilder = SpannableStringBuilder()
            stringBuilder.append(description)
            fields.joinTo(stringBuilder, separator = ",\n- ", prefix = "\n- ", postfix = ".")
        }
    }
}