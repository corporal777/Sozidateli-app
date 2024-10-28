package com.example.ui.views.phoneText

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import com.example.app.R
import com.example.extensions.onFocusChanged
import com.example.extensions.onTextChanged
import com.google.android.material.textfield.TextInputEditText

class PhoneFormatEditText : TextInputEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PhoneFormatEditText)
        val hintText = a.getText(R.styleable.PhoneFormatEditText_phoneFormatHint)
        a.recycle()
        setPhoneHint(hintText)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private var onInputFocusChanged: (hasFocus: Boolean) -> Unit = {}
    private var onInputTextChanged: (text: CharSequence?) -> Unit = {}

    private val mask: String = "+7"
    private var editingOnChanged = false
    private var editingAfter = false
    private var textSelection = 0
    private var formattedText = mask

    init {
        onFocusChanged { hasFocus ->
            if (hasFocus) setText(makeMaskedText(formattedText))
            else {
                if (formattedText.isBlank() || formattedText == mask) text = null
            }
        }
        doOnTextChanged { text, start, before, count ->
            if (!editingOnChanged && hasFocus()) editingOnChanged = true
        }
        doAfterTextChanged {
            if (!editingAfter && editingOnChanged) {
                editingAfter = true
                setText(makeMaskedText(it.toString()))
                setSelection(textSelection)
                editingAfter = false
                editingOnChanged = false
            }
            onInputTextChanged.invoke(clearPhoneText(text.toString()))
        }
    }

    private fun makeMaskedText(text: String): String {
        formattedText = clearPhoneText(text)

        if (formattedText.isNullOrBlank()) {
            formattedText = mask
            textSelection = mask.length
        } else if (formattedText.length < mask.length || formattedText == mask) {
            formattedText = mask
            textSelection = mask.length
        } else if (formattedText.length > mask.length && formattedText.contains(mask)) {
            formattedText = formatPhoneText(formattedText)
            textSelection = formattedText.length
        } else {
            formattedText = formatPhoneText(mask + formattedText)
            textSelection = formattedText.length
        }
        return formattedText
    }

    private fun setPhoneHint(text: CharSequence?) {
        hint = formatPhoneText(text.toString())
    }


    fun setPhoneText(text: CharSequence?) {
        if (!text.isNullOrBlank()) {
            setText(formatPhoneText(text.toString()))
            formattedText = formatPhoneText(text.toString())
        }
    }

    private fun clearPhoneText(text: String): String {
        if (text.isNullOrEmpty()) return ""
        else {
            val str = text.replace(" ", "")
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")
            if (str.length > 12) return str.substring(0, 12)
            else return str
        }
    }

    private fun formatPhoneText(text: String?): String {
        if (text.isNullOrBlank()) return ""
        else {
            return if (text.length <= 5)
                StringBuilder(text).insert(2, " ").toString()
            else if (text.length <= 8)
                StringBuilder(text).insert(2, " ").insert(6, " ").toString()
            else if (text.length <= 10) StringBuilder(text)
                .insert(2, " ")
                .insert(6, " ")
                .insert(10, " ").toString()
            else StringBuilder(text)
                .insert(2, " ")
                .insert(6, " ")
                .insert(10, " ")
                .insert(13, " ").toString()

//            return if (text.length <= 5) StringBuilder(text).insert(2, " (").toString()
//            else if (text.length in 5..8)
//                StringBuilder(text)
//                    .insert(2, " (")
//                    .insert(7, ") ").toString()
//            else if (text.length in 8..10)
//                StringBuilder(text)
//                    .insert(2, " (")
//                    .insert(7, ") ")
//                    .insert(12, "-").toString()
//            else StringBuilder(text)
//                .insert(2, " (")
//                .insert(7, ") ")
//                .insert(12, "-")
//                .insert(15, "-").toString()
        }
    }


    fun onInputFocusChanged(onFocusChanged: (hasFocus: Boolean) -> Unit) {
        this.onInputFocusChanged = onFocusChanged
    }

    fun onInputTextChanged(onTextChanged: (text: CharSequence?) -> Unit) {
        this.onInputTextChanged = onTextChanged
    }
}