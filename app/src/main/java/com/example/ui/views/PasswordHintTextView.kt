package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import com.example.R

class PasswordHintTextView : StateTextView {

    private var highlightState = STATE_NORMAL

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun makeNormal() {
        highlightState = STATE_NORMAL
        setState(emptyArray())
    }

    fun highlightCorrect() {
        highlightState = STATE_HIGHLIGHT_CORRECT
        setState(arrayOf(intArrayOf(R.attr.password_hint_state_correct)))
    }

    fun highlightError() {
        highlightState = STATE_HIGHLIGHT_ERROR
        setState(arrayOf(intArrayOf(R.attr.password_hint_state_incorrect)))
    }

    companion object {
        private const val STATE_NORMAL = 0
        private const val STATE_HIGHLIGHT_ERROR = -1
        private const val STATE_HIGHLIGHT_CORRECT = 1
    }
}