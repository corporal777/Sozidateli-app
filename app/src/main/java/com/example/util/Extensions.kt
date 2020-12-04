package com.example.util

import android.widget.CheckBox
import android.widget.EditText
import onTextChanged

fun String.firstLetterToUppercase(): String {
    return if (this.isNotBlank())
        this.substring(0, 1).toUpperCase() + this.substring(1).toLowerCase()
    else
        this
}

fun CheckBox.initSwitch(checked: Boolean, onCheckedChanged: (isChecked: Boolean) -> Unit) {
    isChecked = checked
    setOnCheckedChangeListener { _, isChecked -> onCheckedChanged(isChecked) }
}

fun EditText.initInput(text: String?, onTextChanged: (text: CharSequence?) -> Unit) {
    setText(text)
    onTextChanged(onTextChanged)
}