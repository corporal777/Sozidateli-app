package com.example.ui.views.toolbar

import android.annotation.SuppressLint
import android.content.Context
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.example.app.R
import com.example.common.dp

@SuppressLint("ViewConstructor")
class ToolbarIconView(
    context: Context,
    val size: Int = 0
) : AppCompatImageView(context) {

    init {
        background = ContextCompat.getDrawable(context, R.drawable.btn_background_toolbar)
        if (size == 0) layoutParams = LinearLayout.LayoutParams(35.dp, 35.dp)
        else layoutParams = LinearLayout.LayoutParams(size.dp, size.dp)
        initPadding(5.dp, 5.dp, 5.dp, 5.dp)
    }

    fun initPadding(top: Int, bottom: Int, left: Int, right: Int) {
        setPadding(left, top, right, bottom)
    }

    fun setImageAsIcon(image: Int) {
        setImageResource(image)
    }

    fun setIconTint(tint : Int){
        imageTintList = ContextCompat.getColorStateList(context, tint);
    }

}