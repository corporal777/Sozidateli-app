package com.example.ui.views.toolbar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.example.R
import com.example.extensions.dp

@SuppressLint("ViewConstructor")
class ToolbarIconView(
    context: Context,
    val size: Int = 0
) : AppCompatImageView(context) {

    init {
        background = ContextCompat.getDrawable(context, R.drawable.custom_toolbar_btn_background_selectable)
        if (size == 0) layoutParams = LinearLayout.LayoutParams(35.dp, 35.dp)
        else layoutParams = LinearLayout.LayoutParams(size.dp, size.dp)
        initPadding(5.dp, 5.dp, 5.dp, 5.dp)
    }

    private fun initPadding(top: Int, bottom: Int, left: Int, right: Int) {
        setPadding(left, top, right, bottom)
    }

    fun setImageAsIcon(image: Int) {
        setImageResource(image)
    }

    fun setAlphaVision(enabled: Boolean) {
        this.apply {
            isEnabled = enabled
            alpha = if (enabled) 1f
            else 0.6f
        }
    }
}