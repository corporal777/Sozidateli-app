package com.example.ui.views.phoneText

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doBeforeTextChanged
import onFocusChanged

class PhoneNumberEditText : AppCompatEditText, TextWatcher {

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {}

    private var onInputFocusChanged: (hasFocus: Boolean) -> Unit = {}
    private var onInputTextChanged: (text: CharSequence?) -> Unit = {}

    private val mask: String = "+79"
    private var charRepresentation = '#'
    private var rawToMask: IntArray = IntArray(0)
    private var rawText = RawText()
    private var editingBefore = false
    private var editingOnChanged = false
    private var editingAfter = false
    private var maskToRaw: IntArray = IntArray(0)
    private var textSelection = 0
    private var initialized = false
    private var ignore = false
    private var maxRawLength = 0
    private var lastValidMaskPosition = 0
    private var selectionChanged = false
    private var allowedChars: String? = "-1234567890"
    private var deniedChars: String? = null
    private var isKeepingText = false

    private var maskedText = ""

    init {
        onFocusChanged { hasFocus ->
            if (hasFocus) {
                removeTextChangedListener(this)
                setText(formatPhoneText(mask))
                addTextChangedListener(this)
            }
            onInputFocusChanged.invoke(hasFocus)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        if (!editingOnChanged) editingOnChanged = true
    }

    override fun afterTextChanged(s: Editable?) {
        if (!editingAfter && editingOnChanged) {
            editingAfter = true
            setText(makeMaskedText(s.toString()))
            setSelection(textSelection)
            editingAfter = false
            editingOnChanged = false
        }

        onInputTextChanged.invoke(clearPhoneText(text.toString()))
    }

    private fun makeMaskedText(text: String?): String? {
        var formattedText = clearPhoneText(text.toString())
        if (formattedText.isNullOrBlank() || formattedText.length in 2..2 || formattedText == mask) {
            textSelection = 0
            formattedText = null
        }
        else if (formattedText.length > 3 && formattedText.contains(mask)) {
            formattedText = formatPhoneText(formattedText)
            textSelection = formattedText?.length ?: 0
        }
        else {
            formattedText = formatPhoneText(mask + formattedText)
            textSelection = formattedText?.length ?: 0
        }
        return formattedText
    }

    private fun clearPhoneText(text : String?): String? {
        if (text.isNullOrEmpty()) return null
        else {
            val str = text.replace(" ", "")
            if (str.length > 12) return str.substring(0, 12)
            else return str
        }
    }

    private fun formatPhoneText(text : String?): String? {
        if (text.isNullOrBlank()) return null
        else {
            if (text.length <= 5) {
                return StringBuilder(text).insert(2, " ").toString()
            } else if (text.length <= 8){
                return StringBuilder(text).insert(2, " ").insert(6, " ").toString()
            } else if (text.length <= 10){
                return StringBuilder(text)
                    .insert(2, " ")
                    .insert(6, " ")
                    .insert(10, " ").toString()
            } else return StringBuilder(text)
                .insert(2, " ")
                .insert(6, " ")
                .insert(10, " ")
                .insert(13, " ").toString()
        }
    }


    fun onInputFocusChanged(onFocusChanged: (hasFocus: Boolean) -> Unit) {
        this.onInputFocusChanged = onFocusChanged
    }

    fun onInputTextChanged(onTextChanged: (text: CharSequence?) -> Unit) {
        this.onInputTextChanged = onTextChanged
    }

    companion object {
        const val SPACE = " "
    }
}