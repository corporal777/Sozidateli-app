package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.appbar.AppBarLayout

class CustomAppBarLayout : AppBarLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
    }

    fun changeAppBarElevation(value: Float) {
        elevation = if (value <= 10f) value else 10f
    }
}