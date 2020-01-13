package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.R
import com.example.extensions.dp
import com.google.android.material.textfield.TextInputLayout

class CustomTextInputLayout : TextInputLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        findViewById<View>(R.id.text_input_end_icon)?.apply {
            minimumHeight = 42.dp
            minimumWidth = 38.dp
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun setErrorEnabled(enabled: Boolean) {
        super.setErrorEnabled(enabled)
        if (!enabled) return
        findViewById<TextView>(R.id.textinput_error)?.apply {
            (parent as? ViewGroup)?.apply {
                this.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    this.gravity = Gravity.CENTER
                }
            }
            this.gravity = Gravity.CENTER
        }
    }
}