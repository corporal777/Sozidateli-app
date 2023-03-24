package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.core.view.isVisible
import com.google.android.material.appbar.AppBarLayout
import offsetChangedListener
import kotlin.math.abs

class CustomAppBarLayout : AppBarLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        if (isVisible) {
            offsetChangedListener { appBarLayout, offset ->
                Log.e("OFFSET", offset.toString())
            }
        }

    }

    fun changeAppBarElevation(value: Float) {
        elevation = if (value <= 10f) value
        else 10f
    }

    fun setScrollOffset(value : Int){
       changeAppBarElevation(abs(value / 10f))
    }

}