package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.example.R
import com.example.extensions.dp
import com.google.android.material.textfield.TextInputLayout

class ResizableIconsTexInputLayout : TextInputLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        findViewById<View>(R.id.text_input_end_icon)?.apply {
            minimumHeight = 42.dp
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}