package com.example.util

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView

class SearchInput(val view: EditText) {

    private var onAfterTextChange: OnAfterTextChange? = null
    private var onFocusChange: OnFocusChange? = null
    private var onTextChangeSearch: OnTextChangeDone? = null

    init {
        view.apply {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    onAfterTextChange?.invoke(s.toString())
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })
            onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                onFocusChange?.invoke(hasFocus)
            }
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

    fun setOnAfterTextChange(onAfterTextChange: OnAfterTextChange) {
        this.onAfterTextChange = onAfterTextChange
    }

    fun setOnTextChangeDone(onTextChangeDone: OnTextChangeDone) {
        this.onTextChangeSearch = onTextChangeDone
    }

    fun setOnFocusChange(onFocusChange: OnFocusChange) {
        this.onFocusChange = onFocusChange
    }
}

typealias OnTextChange = (String) -> Unit
typealias OnAfterTextChange = (String) -> Unit
typealias OnFocusChange = (Boolean) -> Unit
typealias OnTextChangeDone = (String) -> Unit