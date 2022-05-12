package com.example.util

import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView

class SearchInput(
        val view: EditText
) {

    private var onTextChange: OnTextChange? = null
    private var onTextChangeSearch: OnTextChangeDone? = null

    init {
        view.apply {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    onTextChange?.invoke(s.toString())
                }
            })

            setOnEditorActionListener(TextView.OnEditorActionListener { textView, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    return@OnEditorActionListener onTextChangeSearch?.let {
                        it.invoke(textView.text.toString())
                        true
                    } ?: false
                }
                return@OnEditorActionListener false
            })
        }
    }

    fun setOnTextChange(onTextChange: OnTextChange) {
        this.onTextChange = onTextChange
    }

    fun setOnTextChangeDone(onTextChangeDone: OnTextChangeDone) {
        this.onTextChangeSearch = onTextChangeDone
    }
}

typealias OnTextChange = (String) -> Unit
typealias OnTextChangeDone = (String) -> Unit