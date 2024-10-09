package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.app.R
import com.example.extensions.dp

class CustomProgressView : LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)



    init {
        background = ContextCompat.getDrawable(context, R.drawable.ic_star_btn_background)
    }

    fun showProgressBar(){
        val progressBar = CustomProgressBar(context)
        addView(progressBar, 0)
        isVisible = true
    }

    fun hideProgressBar(){
        isVisible = false
        removeAllViews()
    }
}